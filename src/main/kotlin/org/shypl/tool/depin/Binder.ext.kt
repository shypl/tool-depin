package org.shypl.tool.depin

import org.shypl.tool.depin.internal.findAnnotationDirect
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

inline fun <reified T : Any> Binder.bind(instance: T) =
	bindInstance(T::class, instance)

inline fun <reified T : Any, reified D : T> Binder.bind() =
	bind(T::class, D::class)

inline fun <reified T : Any> Binder.bind(noinline producer: () -> T) =
	bind(T::class, producer)

inline fun <reified T : Any> Binder.bindInjected(noinline producer: Injector.() -> T) =
	bindInjected(T::class, producer)


inline fun <reified T : Any, reified D : T> Binder.bindSupplier() =
	bindSupplier(T::class, D::class)

inline fun <reified T : Any> Binder.bindSupplier(noinline producer: () -> T) =
	bindSupplier(T::class, producer)

inline fun <reified T : Any> Binder.bindSupplierInjected(noinline producer: Injector.() -> T) =
	bindSupplierInjected(T::class, producer)


inline fun <T : Any, reified D : T> Binder.bind(name: NamedType<T>) =
	bind(name, D::class)

inline fun <T : Any, reified D : T> Binder.bindSupplier(name: NamedType<T>) =
	bindSupplier(name, D::class)


inline fun <reified T : Any> Binder.bindFactory(noinline init: Binder.Factory.() -> Unit = {}) =
	bindFactory(T::class, init)

inline fun <reified T : Any> Binder.bindFactorySupplier(noinline init: Binder.Factory.() -> Unit = {}) =
	bindFactorySupplier(T::class, init)


inline fun <T : Any, reified F : T> Binder.bindFactory(name: NamedType<T>, noinline init: Binder.Factory.() -> Unit = {}) =
	bindFactory(name, F::class, init)

inline fun <T : Any, reified F : T> Binder.bindFactorySupplier(name: NamedType<T>, noinline init: Binder.Factory.() -> Unit = {}) =
	bindFactorySupplier(name, F::class, init)

inline fun <reified T : Any, reified I : T> Binder.Factory.bind() =
	bind(T::class, I::class)


fun <A : Annotation> Binder.addSmartProducerForAnnotatedClassInject(annotationClass: KClass<A>, producer: Injector.(A, KClass<*>) -> Any?) =
	addSmartProducerForClassInjected { clazz ->
		clazz.findAnnotationDirect(annotationClass)?.let {
			producer(it, clazz)
		}
	}

fun <A : Annotation> Binder.addSmartProducerForAnnotatedClass(annotationClass: KClass<A>, producer: (A, KClass<*>) -> Any?) =
	addSmartProducerForClass { clazz ->
		clazz.findAnnotationDirect(annotationClass)?.let {
			producer(it, clazz)
		}
	}

fun <A : Annotation> Binder.addSmartProducerForAnnotatedParameterInject(annotationClass: KClass<A>, producer: Injector.(A, KParameter) -> Any?) =
	addSmartProducerForParameterInjected { parameter ->
		parameter.findAnnotationDirect(annotationClass)?.let {
			producer(it, parameter)
		}
	}

fun <A : Annotation> Binder.addSmartProducerForAnnotatedParameter(annotationClass: KClass<A>, producer: (A, KParameter) -> Any?) =
	addSmartProducerForParameter { parameter ->
		parameter.findAnnotationDirect(annotationClass)?.let {
			producer(it, parameter)
		}
	}

inline fun <reified A : Annotation> Binder.addSmartProducerForAnnotatedClass(noinline producer: (A, KClass<*>) -> Any?) =
	addSmartProducerForAnnotatedClass(A::class, producer)

inline fun <reified A : Annotation> Binder.addSmartProducerForAnnotatedClassInject(noinline producer: Injector.(A, KClass<*>) -> Any?) =
	addSmartProducerForAnnotatedClassInject(A::class, producer)


inline fun <reified A : Annotation> Binder.addSmartProducerForAnnotatedParameter(noinline producer: (A, KParameter) -> Any?) =
	addSmartProducerForAnnotatedParameter(A::class, producer)

inline fun <reified A : Annotation> Binder.addSmartProducerForAnnotatedParameterInject(noinline producer: (Injector, A, KParameter) -> Any?) =
	addSmartProducerForAnnotatedParameterInject(A::class, producer)


inline fun <reified T : Any> Binder.addProduceObserverBefore(crossinline observer: (KClass<out T>) -> Unit) =
	addProduceObserverBefore { clazz ->
		if (clazz.isSubclassOf(T::class)) {
			@Suppress("UNCHECKED_CAST")
			observer(clazz as KClass<out T>)
		}
	}

inline fun <reified T : Any> Binder.addProduceObserverAfter(crossinline observer: (KClass<out T>, T) -> Unit) =
	addProduceObserverAfter { clazz, obj ->
		if (clazz.isSubclassOf(T::class)) {
			@Suppress("UNCHECKED_CAST")
			observer(clazz as KClass<out T>, obj as T)
		}
	}