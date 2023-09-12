package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertEquals

class TestBindImplementation {
	
	@Test
	fun self() {
		val injector = Injector { bind<StubEmpty, StubEmpty>() }
		
		val a = injector.get<StubEmpty>()
		val b = injector.get<StubEmpty>()
		
		assertEquals(a, b)
	}
	
	@Test
	fun impl() {
		val injector = Injector { bind<StubStorage, StubStorageImpl>() }
		
		val a = injector.get<StubStorage>()
		val b = injector.get<StubStorage>()
		
		assertEquals(a, b)
	}
	
	//
	
	@Test
	fun named_self() {
		val name = stubNameEmpty
		val injector = Injector { bind(name) }
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertEquals(a, b)
	}
	
	
	@Test
	fun named_impl() {
		val name = stubNameStorage
		val injector = Injector { bind<StubStorage, StubStorageImpl>(name) }
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertEquals(a, b)
	}
}