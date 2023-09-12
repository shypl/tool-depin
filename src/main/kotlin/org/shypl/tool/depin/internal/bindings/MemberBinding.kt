package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.internal.Binding
import kotlin.reflect.KCallable

internal class MemberBinding<T : Any>(
	private val obj: Any,
	private val member: KCallable<*>,
) : Binding<T> {
	override fun get(): T {
		@Suppress("UNCHECKED_CAST")
		return member.call(obj) as T
	}
}