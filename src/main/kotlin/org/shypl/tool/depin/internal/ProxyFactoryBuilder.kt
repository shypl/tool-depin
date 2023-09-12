package org.shypl.tool.depin.internal

import org.shypl.tool.depin.Binder
import org.shypl.tool.depin.Implementation
import org.shypl.tool.depin.InjectException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

internal class ProxyFactoryBuilder<T : Any>(
	private val clazz: KClass<T>,
) : Binder.Factory {
	
	private var members: List<ProxyFactoryMember> = clazz.publicDeclaredMembers.map {
		if (it is KFunction<*>)
			ProxyFactoryMember(clazz, it, it.findAnnotation<Implementation>()?.type ?: it.returnType.jvmErasure)
		else
			throw InjectException("Only function allowed for proxy")
	}
	
	override fun <T : Any> bind(clazz: KClass<T>, implementation: KClass<out T>): Binder.Factory {
		val memberProxy = members.find { it.member.returnType.jvmErasure == clazz }
			?: throw InjectException("Member for return type '${clazz}' not found in type '${this.clazz}'")
		memberProxy.target = implementation
		return this
	}
	
	fun build(injector: InjectorImpl): T {
		return createProxyFactory(injector, clazz, members)
	}
}