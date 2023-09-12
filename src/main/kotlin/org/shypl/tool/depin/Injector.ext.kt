package org.shypl.tool.depin

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters

inline fun <reified T : Any> Injector.get(): T {
	return get(T::class)
}

inline fun <reified T : Any> Injector.get(name: String): T {
	return get(T::class, name)
}

fun <T : Any> Injector.get(type: KClass<T>, name: String): T {
	return get(NamedType(type, name))
}

fun <T> Injector.inject(callable: KCallable<T>): (target: Any) -> T {
	return { target ->
		val args = callable.valueParameters.map(this::get)
		callable.call(target, *args.toTypedArray())
	}
}