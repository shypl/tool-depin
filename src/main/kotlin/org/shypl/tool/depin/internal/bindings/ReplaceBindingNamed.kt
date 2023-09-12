package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.NamedType
import org.shypl.tool.depin.internal.InjectorImpl

internal class ReplaceBindingNamed<T : Any>(
	private val name: NamedType<T>,
	injector: InjectorImpl,
	producer: InjectorImpl.() -> T,
) : ReplaceBinding<T>(injector, producer) {
	override fun replace(binding: InstanceBinding<T>) {
		injector.registry.setBinding(name, binding)
	}
}

