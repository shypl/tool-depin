package org.shypl.tool.depin.internal

import org.shypl.tool.depin.Binder
import org.shypl.tool.depin.InjectException
import org.shypl.tool.depin.Injector
import org.shypl.tool.depin.NamedType
import org.shypl.tool.depin.internal.bindings.ImplementationBinding
import org.shypl.tool.depin.internal.bindings.InstanceBinding
import org.shypl.tool.depin.internal.bindings.ReplaceBindingNamed
import org.shypl.tool.depin.internal.bindings.ReplaceBindingTyped
import org.shypl.tool.depin.internal.bindings.SupplierBinding
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal class BinderImpl(
	private val injector: InjectorImpl,
	private val strong: Boolean,
) : Binder {
	
	override fun <T : Any> bindInstance(clazz: KClass<T>, instance: T) {
		check(clazz)
		injector.registry.setBinding(clazz, InstanceBinding(instance))
	}
	
	override fun <T : Any> bind(clazz: KClass<T>, implementation: KClass<out T>) {
		if (clazz == implementation) {
			bind(clazz) { injector.make(implementation) }
		}
		else {
			bindInjected(clazz) { get(implementation) }
		}
	}
	
	override fun <T : Any> bind(clazz: KClass<T>, producer: () -> T) {
		bindInjected(clazz) { producer() }
	}
	
	override fun <T : Any> bindInjected(clazz: KClass<T>, producer: Injector.() -> T) {
		check(clazz)
		injector.registry.setBinding(clazz, ReplaceBindingTyped(clazz, injector, producer))
	}
	
	override fun <T : Any> bindSupplier(clazz: KClass<T>, implementation: KClass<out T>) {
		check(clazz)
		if (clazz == implementation) {
			bindSupplier(clazz) { injector.make(implementation) }
		}
		else {
			injector.registry.setBinding(clazz, ImplementationBinding(injector, implementation))
		}
	}
	
	override fun <T : Any> bindSupplier(clazz: KClass<T>, producer: () -> T) {
		bindSupplierInjected(clazz) { producer() }
	}
	
	override fun <T : Any> bindSupplierInjected(clazz: KClass<T>, function: Injector.() -> T) {
		check(clazz)
		injector.registry.setBinding(clazz, SupplierBinding(injector, function))
	}
	
	
	override fun <T : Any> bindInstance(name: NamedType<T>, instance: T) {
		check(name)
		injector.registry.setBinding(name, InstanceBinding(instance))
	}
	
	override fun <T : Any> bind(name: NamedType<T>, implementation: KClass<out T>) {
		bindInjected(name) { get(implementation) }
	}
	
	override fun <T : Any> bind(name: NamedType<T>, producer: () -> T) {
		bindInjected(name) { producer() }
	}
	
	override fun <T : Any> bindInjected(name: NamedType<T>, producer: Injector.() -> T) {
		check(name)
		injector.registry.setBinding(name, ReplaceBindingNamed(name, injector, producer))
	}
	
	
	override fun <T : Any> bindSupplier(name: NamedType<T>, implementation: KClass<out T>) {
		check(name)
		injector.registry.setBinding(name, ImplementationBinding(injector, implementation))
	}
	
	override fun <T : Any> bindSupplier(name: NamedType<T>, producer: () -> T) {
		bindSupplierInjected(name) { producer() }
	}
	
	override fun <T : Any> bindSupplierInjected(name: NamedType<T>, producer: Injector.() -> T) {
		check(name)
		injector.registry.setBinding(name, SupplierBinding(injector, producer))
	}
	
	
	override fun <T : Any> bindFactory(clazz: KClass<T>, init: (Binder.Factory) -> Unit) {
		check(clazz)
		injector.registry.setBinding(clazz, ReplaceBindingTyped(clazz, injector, createProxyFactory(clazz, init)))
	}
	
	override fun <T : Any> bindFactorySupplier(clazz: KClass<T>, init: (Binder.Factory) -> Unit) {
		check(clazz)
		injector.registry.setBinding(clazz, SupplierBinding(injector, createProxyFactory(clazz, init)))
	}
	
	override fun <T : Any> bindFactory(name: NamedType<T>, clazz: KClass<out T>, init: (Binder.Factory) -> Unit) {
		check(name)
		injector.registry.setBinding(name, ReplaceBindingNamed(name, injector, createProxyFactory(clazz, init)))
	}
	
	override fun <T : Any> bindFactorySupplier(name: NamedType<T>, clazz: KClass<out T>, init: (Binder.Factory) -> Unit) {
		check(name)
		injector.registry.setBinding(name, SupplierBinding(injector, createProxyFactory(clazz, init)))
	}
	
	private fun <T : Any> createProxyFactory(factoryClass: KClass<T>, init: (Binder.Factory) -> Unit): (InjectorImpl) -> T {
		return ProxyFactoryBuilder(factoryClass).apply(init)::build
	}
	
	
	override fun addSmartProducerForClass(producer: (KClass<*>) -> Any?) {
		addSmartProducerForClassInjected { it -> producer(it) }
	}
	
	override fun addSmartProducerForClassInjected(producer: Injector.(KClass<*>) -> Any?) {
		injector.registry.addSmartProducerForClass(producer)
	}
	
	override fun addSmartProducerForParameter(producer: (KParameter) -> Any?) {
		addSmartProducerForParameterInjected { it -> producer(it) }
	}
	
	override fun addSmartProducerForParameterInjected(producer: Injector.(KParameter) -> Any?) {
		injector.registry.addSmartProducerForParameter(producer)
	}
	
	
	override fun addProduceObserverBefore(observer: (KClass<*>) -> Unit) {
		injector.registry.addProduceObserver(observer)
	}
	
	override fun addProduceObserverAfter(observer: (KClass<*>, Any) -> Unit) {
		injector.registry.addProduceObserver(observer)
	}
	
	private fun check(type: KClass<*>) {
		if (strong && injector.registry.hasBinding(type)) {
			throw InjectException("Type '$type' is already binded")
		}
	}
	
	private fun check(name: NamedType<*>) {
		if (strong && injector.registry.hasBinding(name)) {
			throw InjectException("Name '$name' is already binded")
		}
	}
}