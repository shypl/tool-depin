package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertNotEquals

class TestBindWeakImplementation {
	@Test
	fun self() {
		val injector = Injector { bindSupplier<StubEmpty, StubEmpty>() }
		
		val a = injector.get<StubEmpty>()
		val b = injector.get<StubEmpty>()
		
		assertNotEquals(a, b)
	}
	
	@Test
	fun impl() {
		val injector = Injector { bindSupplier<StubStorage, StubStorageImpl>() }
		
		val a = injector.get<StubStorage>()
		val b = injector.get<StubStorage>()
		
		assertNotEquals(a, b)
	}
	
	//
	
	@Test
	fun named_self() {
		val name = stubNameEmpty
		val injector = Injector { bindSupplier(name) }
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertNotEquals(a, b)
	}
	
	
	@Test
	fun named_impl() {
		val name = stubNameStorage
		val injector = Injector { bindSupplier<StubStorage, StubStorageImpl>(name) }
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertNotEquals(a, b)
	}
}