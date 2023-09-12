package org.shypl.tool.depin

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface Injector {
	fun <T : Any> get(clazz: KClass<T>): T
	
	fun <T : Any> get(name: NamedType<T>): T
	
	fun get(parameter: KParameter): Any
	
	companion object {
		operator fun invoke(strong: Boolean = true, init: Binder.() -> Unit): Injector {
			return Injection(init).build(strong)
		}
	}
}