package org.shypl.tool.depin

import kotlin.reflect.KClass

data class NamedType<T : Any>(
	val type: KClass<T>,
	val name: String,
) {
	override fun toString(): String {
		return "$name:${type.qualifiedName}"
	}
}

inline fun <reified T : Any> String.asNamedType(): NamedType<T> {
	return NamedType(T::class, this)
}
