package org.shypl.tool.depin.internal

import kotlin.reflect.KClass

internal class ProducingStack : Iterable<KClass<*>> {
	private val list = object : ThreadLocal<MutableList<KClass<*>>>() {
		override fun initialValue(): MutableList<KClass<*>> = mutableListOf()
	}
	
	fun contains(clazz: KClass<*>): Boolean {
		return list.get().contains(clazz)
	}
	
	fun add(clazz: KClass<*>) {
		list.get().add(clazz)
	}
	
	fun remove() {
		list.get().apply { removeAt(lastIndex) }
	}
	
	override fun iterator(): Iterator<KClass<*>> {
		return list.get().iterator()
	}
}
