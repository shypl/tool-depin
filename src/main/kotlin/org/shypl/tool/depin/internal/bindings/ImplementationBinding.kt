package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.internal.InjectorImpl
import kotlin.reflect.KClass

internal class ImplementationBinding<T : Any>(
	injector: InjectorImpl,
	private val target: KClass<out T>,
) : InjectedBinding<T>(injector) {
	override fun get(): T {
		return injector.get(target)
	}
}