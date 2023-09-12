package org.shypl.tool.depin.internal.bindings

import org.shypl.tool.depin.internal.Binding
import org.shypl.tool.depin.internal.InjectorImpl

internal abstract class InjectedBinding<T : Any>(
	protected val injector: InjectorImpl,
) : Binding<T>

