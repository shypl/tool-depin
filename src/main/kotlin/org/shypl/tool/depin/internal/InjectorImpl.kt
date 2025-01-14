package org.shypl.tool.depin.internal

import org.shypl.tool.depin.Bind
import org.shypl.tool.depin.Factory
import org.shypl.tool.depin.Implementation
import org.shypl.tool.depin.InjectException
import org.shypl.tool.depin.Injector
import org.shypl.tool.depin.Named
import org.shypl.tool.depin.NamedType
import org.shypl.tool.depin.internal.bindings.InstanceBinding
import org.shypl.tool.depin.internal.bindings.MemberBinding
import org.shypl.tool.logging.Logging
import org.shypl.tool.logging.getLogger
import org.shypl.tool.logging.trace
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

internal class InjectorImpl : Injector {
	
	val registry = Registry()
	
	private val logger = Logging.getLogger<Injector>()
	private val producingStack = ProducingStack()
	
	override fun <T : Any> get(clazz: KClass<T>): T {
		logger.trace { "Getting '${clazz.qualifiedName}'" }
		
		val binding = registry.getBinding(clazz)
		
		return binding
			?.run { get() }
			?: make(clazz)
	}
	
	override fun <T : Any> get(name: NamedType<T>): T {
		logger.trace { "Getting '$name'" }
		
		val binding = registry.getBinding(name)
			?: throw InjectException("Binding for '$name' is not defined")
		
		return binding.get()
	}
	
	override fun get(parameter: KParameter): Any {
		val name = parameter.name!!
		val type = parameter.type.jvmErasure
		
		parameter.findAnnotation<Named>()?.let {
			val n = it.name.ifEmpty { name }
			logger.trace { "Supple parameter '$name:${type.qualifiedName}' by name '$n'" }
			return get(NamedType(type, n))
		}
		
		registry.trySmartProduce(this, parameter)?.let {
			logger.trace { "Supplied parameter '$name:${type.qualifiedName}' from smart producer" }
			return it
		}
		
		logger.trace { "Supple parameter '$name:${type.qualifiedName}' from injector" }
		
		val binding = registry.getBinding(type)
		
		if (binding != null) {
			return binding.get()
		}
		
		return registry.findBindingSimilar(name, type)
			?.run {
				logger.trace { "Found parameter similar '$name:${type.qualifiedName}'" }
				get()
			}
			?: make(type)
	}
	
	fun <T : Any> make(clazz: KClass<T>): T {
		val obj = supple(clazz)
		val binding = InstanceBinding(obj)
		
		clazz.findAnnotation<Bind>()?.also {
			if (it.name.isEmpty()) {
				logger.trace { "Bind self '${clazz.qualifiedName}'" }
				registry.setBinding(clazz, binding)
			}
			else {
				logger.trace { "Bind self '${clazz.qualifiedName}' by name '${it.name}'" }
				registry.setBinding(NamedType(clazz, it.name), binding)
			}
		}
		
		clazz.getSupertypesWithAnnotation<Bind>().forEach {
			//TODO Bind by name
			logger.trace { "Bind self '${clazz.qualifiedName}' as '${it.qualifiedName}'" }
			registry.setBinding(it, binding)
		}
		
		clazz.publicDeclaredMembers
			.filter { it.hasAnnotation<Bind>() }
			.forEach {
				val t = it.returnType.jvmErasure
				val a = it.findAnnotation<Bind>()!!
				val b = MemberBinding<T>(obj, it)
				
				if (a.name.isEmpty()) {
					logger.trace { "Bind member '${clazz.qualifiedName}::${it.name}' as '$t'" }
					registry.setBinding(t, b)
				}
				else {
					logger.trace { "Bind member '${clazz.qualifiedName}::${it.name}' as '$t' by name '${a.name}'" }
					registry.setBinding(NamedType(t, a.name), b)
				}
			}
		
		return obj
	}
	
	private fun <T : Any> supple(clazz: KClass<T>): T {
		
		clazz.findAnnotation<Implementation>()?.let {
			logger.trace { "Supple '${clazz.qualifiedName}' as delegate '${it.type.qualifiedName}'" }
			
			@Suppress("UNCHECKED_CAST")
			return get(it.type) as T
		}
		
		if (clazz.hasAnnotation<Factory>()) {
			logger.trace { "Supple '${clazz.qualifiedName}' as proxy factory" }
			return ProxyFactoryBuilder(clazz).build(this)
		}
		
		registry.trySmartProduce(this, clazz)?.let {
			logger.trace { "Supplied '${clazz.qualifiedName}' from smart producer" }
			@Suppress("UNCHECKED_CAST")
			return it as T
		}
		
		return produce(clazz)
	}
	
	fun <T : Any> produce(clazz: KClass<T>): T {
		return produce(clazz) {
			it.valueParameters.map(::get)
		}
	}
	
	fun <T : Any> produce(clazz: KClass<T>, argFactories: List<ProxyFactoryMemberArgument>, incomingArgs: Array<*>): T {
		return produce(clazz) {
			argFactories.map { it.invoke(this, incomingArgs) }
		}
	}
	
	private inline fun <T : Any> produce(clazz: KClass<T>, argsExtractor: (KFunction<T>) -> List<*>): T {
		logger.trace { "Produce '${clazz.qualifiedName}'" }
		
		clazz.checkClassInjectable()
		
		val circular = producingStack.contains(clazz)
		producingStack.add(clazz)
		
		try {
			
			if (circular) {
				throw InjectException("Circular dependency:\n" + producingStack.joinToString("\n") { " - $it" })
			}
			
			val constructor = clazz.primaryConstructor!!
			val args = argsExtractor(constructor)
			
			registry.observeProduceBefore(clazz)
			
			val obj = constructor.call(*args.toTypedArray())
			
			registry.observeProduceAfter(clazz, obj)
			
			return obj
		}
		finally {
			producingStack.remove()
		}
	}
}