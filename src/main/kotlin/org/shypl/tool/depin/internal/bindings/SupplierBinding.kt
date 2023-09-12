package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.internal.InjectorImpl

internal open class SupplierBinding<T : Any>(
	injector: InjectorImpl,
	private val supplier: (InjectorImpl) -> T,
) : InjectedBinding<T>(injector) {
	override fun get(): T {
		return supplier.invoke(injector)
	}
}