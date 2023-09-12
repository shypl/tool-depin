package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.internal.Binding

internal class InstanceBinding<T : Any>(
	private val instance: T,
) : Binding<T> {
	override fun get(): T {
		return instance
	}
}