package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertEquals

class TestTypedName {
	
	@Test
	fun name() {
		val value = "name".asNamedType<Int>()
		assertEquals("name", value.name)
	}
	
	@Test
	fun type_Boolean() {
		val value = "name".asNamedType<Boolean>()
		assertEquals(Boolean::class, value.type)
	}
	
	@Test
	fun type_Int() {
		val value = "name".asNamedType<Int>()
		assertEquals(Int::class, value.type)
	}
	
	@Test
	fun type_Float() {
		val value = "name".asNamedType<Float>()
		assertEquals(Float::class, value.type)
	}
	
	@Test
	fun type_Long() {
		val value = "name".asNamedType<Long>()
		assertEquals(Long::class, value.type)
	}
	
	@Test
	fun type_Double() {
		val value = "name".asNamedType<Double>()
		assertEquals(Double::class, value.type)
	}
	
	@Test
	fun type_String() {
		val value = "name".asNamedType<String>()
		assertEquals(String::class, value.type)
	}
	
	@Test
	fun type_Interface() {
		val value = "name".asNamedType<StubStorage>()
		assertEquals(StubStorage::class, value.type)
	}
	
	@Test
	fun type_Class() {
		val value = "name".asNamedType<StubEmpty>()
		assertEquals(StubEmpty::class, value.type)
	}
}