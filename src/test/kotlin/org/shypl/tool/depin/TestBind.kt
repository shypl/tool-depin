package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestBind {
	
	@Test
	fun typed() {
		val obj = StubEmpty()
		val injector = Injector { bind(obj) }
		
		assertEquals(obj, injector.get())
	}
	
	@Test
	fun named() {
		val obj = StubEmpty()
		val name = stubNameEmpty
		
		val injector = Injector { bindInstance(name, obj) }
		
		assertEquals(obj, injector.get(name))
	}
	
	@Test
	fun fail_on_strong_typed() {
		val builder = Injection()
		
		builder.configure {
			bind(StubEmpty())
			bind(StubEmpty())
		}
		
		assertFailsWith<InjectException> {
			builder.build(true)
		}
	}
	
	@Test
	fun fail_on_strong_named() {
		val name = stubNameEmpty
		
		val builder = Injection()
		
		builder.configure {
			bindInstance(name, StubEmpty())
			bindInstance(name, StubEmpty())
		}
		
		assertFailsWith<InjectException> {
			builder.build(true)
		}
	}
	
	@Test
	fun success_on_not_strong_typed() {
		val builder = Injection()
		
		builder.configure {
			bind(StubEmpty())
			bind(StubEmpty())
		}
		
		builder.build(false)
	}
	
	@Test
	fun success_on_not_strong_named() {
		val builder = Injection()
		
		val name = stubNameEmpty
		
		builder.configure {
			bindInstance(name, StubEmpty())
			bindInstance(name, StubEmpty())
		}
		
		builder.build(false)
	}
}