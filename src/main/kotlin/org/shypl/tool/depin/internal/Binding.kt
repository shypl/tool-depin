package org.shypl.tool.depin.internal

internal interface Binding<T : Any> {
	fun get(): T
}

