package org.shypl.tool.depin.internal

import org.shypl.tool.depin.Injector
import org.shypl.tool.depin.NamedType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

internal class Registry {
	private val classBindings = ConcurrentHashMap<KClass<*>, Binding<*>>()
	private val nameBindings = ConcurrentHashMap<NamedType<*>, Binding<*>>()
	private val smartClassProducers = ConcurrentLinkedQueue<Injector.(KClass<*>) -> Any?>()
	private val smartParameterProducers = ConcurrentLinkedQueue<Injector.(KParameter) -> Any?>()
	private val produceObserversBefore = ConcurrentLinkedQueue<(KClass<*>) -> Unit>()
	private val produceObserversAfter = ConcurrentLinkedQueue<(KClass<*>, Any) -> Unit>()
	
	
	fun hasBinding(clazz: KClass<*>): Boolean {
		return classBindings.containsKey(clazz)
	}
	
	fun hasBinding(name: NamedType<*>): Boolean {
		return nameBindings.containsKey(name)
	}
	
	fun setBinding(clazz: KClass<*>, binding: Binding<*>) {
		classBindings[clazz] = binding
	}
	
	fun setBinding(name: NamedType<*>, binding: Binding<*>) {
		nameBindings[name] = binding
	}
	
	fun addSmartProducerForClass(producer: Injector.(KClass<*>) -> Any?) {
		smartClassProducers.add(producer)
	}
	
	fun addSmartProducerForParameter(producer: Injector.(KParameter) -> Any?) {
		smartParameterProducers.add(producer)
	}
	
	fun addProduceObserver(observer: (KClass<*>) -> Unit) {
		produceObserversBefore.add(observer)
	}
	
	fun addProduceObserver(observer: (KClass<*>, Any) -> Unit) {
		produceObserversAfter.add(observer)
	}
	
	@Suppress("UNCHECKED_CAST")
	fun <T : Any> getBinding(clazz: KClass<T>): Binding<T>? {
		return classBindings[clazz] as Binding<T>?
	}
	
	@Suppress("UNCHECKED_CAST")
	fun <T : Any> getBinding(name: NamedType<T>): Binding<T>? {
		return nameBindings[name] as Binding<T>?
	}
	
	@Suppress("UNCHECKED_CAST")
	fun <T : Any> findBindingSimilar(name: String, clazz: KClass<T>): Binding<T>? {
		nameBindings.forEach {
			if (it.key.name == name && it.key.type.isSubclassOf(clazz)) {
				return it.value as Binding<T>
			}
		}
		classBindings.forEach {
			if (it.key.isSubclassOf(clazz)) {
				return it.value as Binding<T>
			}
		}
		return null
	}
	
	fun trySmartProduce(injector: Injector, clazz: KClass<*>): Any? {
		for (producer in smartClassProducers) {
			producer(injector, clazz)?.let {
				return it
			}
		}
		return null
	}
	
	fun trySmartProduce(injector: Injector, parameter: KParameter): Any? {
		for (producer in smartParameterProducers) {
			producer(injector, parameter)?.let {
				return it
			}
		}
		return null
	}
	
	fun observeProduce(clazz: KClass<*>) {
		produceObserversBefore.forEach { it(clazz) }
	}
	
	fun observeProduce(clazz: KClass<*>, obj: Any) {
		produceObserversAfter.forEach { it(clazz, obj) }
	}
}