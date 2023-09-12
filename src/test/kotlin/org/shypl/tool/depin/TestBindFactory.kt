package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestBindFactory {
	@Test
	fun typed() {
		val injector = Injector {
			bindFactory<StubUserFactory>() {
				bind<StubUser, StubUserImpl>()
			}
		}
		
		val a = injector.get<StubUserFactory>()
		val b = injector.get<StubUserFactory>()
		
		assertEquals(a, b)
	}
	
	@Test
	fun named() {
		val name = stubNameFactory
		
		val injector = Injector {
			bindFactory(name) {
				bind<StubUser, StubUserImpl>()
			}
		}
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertEquals(a, b)
	}
	
	@Test
	fun fail_on_without_provides_declaration() {
		
		val injector = Injector {
			bindFactory<StubUserFactory>()
		}
		
		assertFailsWith<InjectException> {
			injector.get<StubUserFactory>()
		}
	}
	
	
	@Test
	fun fail_on_wrong_provides() {
		
		val builder = Injection().configure {
			bindFactory<StubUserFactory> {
				bind<StubStorage, StubStorageImpl>()
			}
		}
		
		assertFailsWith<InjectException> {
			builder.build()
		}
	}
}