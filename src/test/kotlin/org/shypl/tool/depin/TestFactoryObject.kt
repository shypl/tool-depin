package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TestFactoryObject {
	@Test
	fun check_toString() {
		val injector = Injector {}
		val a = injector.get<StubAutoFactory>().toString()
		
		assertTrue(a.contains("StubAutoFactory\$Proxy"))
	}
	
	@Test
	fun check_hashCode_differed() {
		val injector = Injector {}
		val a = injector.get<StubAutoFactory>().hashCode()
		val b = injector.get<StubAutoFactory>().hashCode()
		
		assertNotEquals(a, b)
	}
	
	@Test
	fun check_hashCode_equals() {
		val injector = Injector {
			bindFactory<StubAutoFactory>()
		}
		
		val a = injector.get<StubAutoFactory>().hashCode()
		val b = injector.get<StubAutoFactory>().hashCode()
		
		assertEquals(a, b)
	}
	
	@Test
	fun check_equals_false() {
		val injector = Injector {}
		val a = injector.get<StubAutoFactory>().hashCode()
		val b = injector.get<StubAutoFactory>().hashCode()
		
		@Suppress("ReplaceCallWithBinaryOperator")
		assertFalse(a.equals(b))
	}
	
	@Test
	fun check_equals_true() {
		val injector = Injector {
			bindFactory<StubAutoFactory>()
		}
		
		val a = injector.get<StubAutoFactory>().hashCode()
		val b = injector.get<StubAutoFactory>().hashCode()
		
		@Suppress("ReplaceCallWithBinaryOperator")
		assertTrue(a.equals(b))
	}
}
