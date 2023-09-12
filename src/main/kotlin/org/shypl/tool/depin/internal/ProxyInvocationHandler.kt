package org.shypl.tool.depin.internal

import org.shypl.tool.depin.InjectException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.reflect.jvm.javaMethod

internal class ProxyInvocationHandler(
	private val typeName: String,
	private val injector: InjectorImpl,
	members: List<ProxyFactoryMember>,
) : InvocationHandler {
	
	private val methods = members.associate {
		it.member.javaMethod!! to MethodInvocation(it)
	}
	
	override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any {
		var m = methods[method]
		
		if (m == null) {
			when (method.name) {
				"toString" -> return "$typeName\$Proxy"
				"equals"   -> return proxy === args!![0]
				"hashCode" -> return hashCode()
			}
			//TODO Find method by name, arguments and return type
			m = methods.keys.find { it.name == method.name }
				?.let { methods[it] }
				?: throw InjectException("Unsupported method '$method'")
		}
		
		return m.invoke(args ?: emptyArray())
	}
	
	private inner class MethodInvocation(method: ProxyFactoryMember) {
		
		private val clazz = method.target
		private val arguments = method.compileArguments()
		
		fun invoke(receivedArgs: Array<Any>): Any {
			return injector.produce(clazz, arguments, receivedArgs)
		}
	}
	
}