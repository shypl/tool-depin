package org.shypl.tool.depin

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface Binder {
	fun <T : Any> bindInstance(clazz: KClass<T>, instance: T)
	
	fun <T : Any> bind(clazz: KClass<T>, implementation: KClass<out T>)
	
	fun <T : Any> bind(clazz: KClass<T>, producer: () -> T)
	
	fun <T : Any> bindInjected(clazz: KClass<T>, producer: Injector.() -> T)
	
	fun <T : Any> bindSupplier(clazz: KClass<T>, implementation: KClass<out T>)
	
	fun <T : Any> bindSupplier(clazz: KClass<T>, producer: () -> T)
	
	fun <T : Any> bindSupplierInjected(clazz: KClass<T>, function: Injector.() -> T)
	
	
	fun <T : Any> bindInstance(name: NamedType<T>, instance: T)
	
	fun <T : Any> bind(name: NamedType<T>, implementation: KClass<out T>)
	
	fun <T : Any> bind(name: NamedType<T>, producer: () -> T)
	
	fun <T : Any> bindInjected(name: NamedType<T>, producer: Injector.() -> T)
	
	fun <T : Any> bindSupplier(name: NamedType<T>, implementation: KClass<out T>)
	
	fun <T : Any> bindSupplier(name: NamedType<T>, producer: () -> T)
	
	fun <T : Any> bindSupplierInjected(name: NamedType<T>, producer: Injector.() -> T)
	
	
	fun <T : Any> bindFactory(clazz: KClass<T>, init: Factory.() -> Unit = {})
	
	fun <T : Any> bindFactorySupplier(clazz: KClass<T>, init: Factory.() -> Unit = {})
	
	
	fun <T : Any> bindFactory(name: NamedType<T>, clazz: KClass<out T>, init: Factory.() -> Unit = {})
	
	fun <T : Any> bindFactorySupplier(name: NamedType<T>, clazz: KClass<out T>, init: Factory.() -> Unit = {})
	
	
	fun addSmartProducerForClass(producer: (KClass<*>) -> Any?)
	
	fun addSmartProducerForClassInjected(producer: Injector.(KClass<*>) -> Any?)
	
	fun addSmartProducerForParameter(producer: (KParameter) -> Any?)
	
	fun addSmartProducerForParameterInjected(producer: Injector.(KParameter) -> Any?)
	
	
	fun addProduceObserverBefore(observer: (KClass<*>) -> Unit)
	
	fun addProduceObserverAfter(observer: (KClass<*>, Any) -> Unit)
	
	
	interface Factory {
		fun <T : Any> bind(clazz: KClass<T>, implementation: KClass<out T>): Factory
	}
}