package org.shypl.tool.depin.internal

import org.shypl.tool.depin.Bind
import org.shypl.tool.depin.Inject
import org.shypl.tool.depin.InjectException
import java.lang.reflect.Proxy
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.jvmErasure

internal fun <T : Any> createProxyFactory(injector: InjectorImpl, clazz: KClass<T>, members: List<ProxyFactoryMember>): T {
	@Suppress("UNCHECKED_CAST")
	return Proxy.newProxyInstance(clazz.java.classLoader, arrayOf(clazz.java), ProxyInvocationHandler(clazz.qualifiedName!!, injector, members)) as T
}

internal fun <T : Annotation> KAnnotatedElement.findAnnotationDirect(clazz: KClass<T>): T? {
	@Suppress("UNCHECKED_CAST")
	return annotations.find { clazz.isInstance(it) } as T?
}

internal fun <T : Any> KClass<T>.extractPrimaryConstructor(): KFunction<T> {
	return primaryConstructor
		?: throw InjectException("Class '$this' not have constructor")
}

fun KClass<*>.checkClassInjectable() {
	if (java.isInterface) {
		throw InjectException("Class '$this' is an interface and cannot be instantiated")
	}
	
	if (!(hasAnnotation<Inject>() || hasAnnotation<Bind>())) {
		throw InjectException("Class '$this' is not injectable and cannot be instantiated")
	}
}

fun KClass<*>.getSupertypesWithAnnotation(clazz: KClass<out Annotation>): List<KClass<*>> {
	val name = FqName(clazz.qualifiedName!!)
	return supertypes
		.asSequence()
		.filter { t ->
			//WTF: JetBrains, why KType not implements KAnnotatedElement?
			(t::class.java.getDeclaredMethod("getType").invoke(t) as? Annotated)?.annotations?.hasAnnotation(name) ?: false
		}
		.map { it.jvmErasure }
		.toList()
}

inline fun <reified T : Annotation> KClass<*>.getSupertypesWithAnnotation() = getSupertypesWithAnnotation(T::class)

inline val KClass<*>.publicDeclaredMembers: Collection<KCallable<*>> get() = declaredMembers.filter { it.visibility == KVisibility.PUBLIC }