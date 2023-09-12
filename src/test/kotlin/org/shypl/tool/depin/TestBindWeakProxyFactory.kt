package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertNotEquals

class TestBindWeakProxyFactory {
	@Test
	fun typed() {
		val injector = Injector {
			bindFactorySupplier<StubUserFactory> {
				bind<StubUser, StubUserImpl>()
			}
		}
		
		val a = injector.get<StubUserFactory>()
		val b = injector.get<StubUserFactory>()
		
		assertNotEquals(a, b)
	}
	
	@Test
	fun named() {
		val name = stubNameFactory
		
		val injector = Injector {
			bindFactorySupplier(name) {
				bind<StubUser, StubUserImpl>()
			}
		}
		
		val a = injector.get(name)
		val b = injector.get(name)
		
		assertNotEquals(a, b)
	}
}