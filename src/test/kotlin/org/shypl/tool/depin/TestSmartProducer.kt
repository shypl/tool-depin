package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertTrue

class TestSmartProducer {
	@Test
	fun for_class() {
		val injector = Injector {
			addSmartProducerForClass {
				return@addSmartProducerForClass if (it == StubStorage::class) StubStorageImpl() else null
			}
		}
		
		val a = injector.get<StubUserManagerImpl>().storage
		
		assertTrue(a is StubStorageImpl)
	}
	
	@Test
	fun for_parameter() {
		val injector = Injector {
			addSmartProducerForParameter {
				return@addSmartProducerForParameter if (it.name == "storage") StubStorageImpl() else null
			}
		}
		
		val a = injector.get<StubUserManagerImpl>().storage
		
		assertTrue(a is StubStorageImpl)
	}
	
	@Test
	fun for_annotated_class() {
		val injector = Injector {
			addSmartProducerForAnnotatedClass<StubClass> { _, _ ->
				StubStorageImpl()
			}
		}
		
		val a = injector.get<StubUserManagerImpl>().storage
		
		assertTrue(a is StubStorageImpl)
	}
	
	@Test
	fun for_annotated_parameter() {
		val injector = Injector {
			addSmartProducerForAnnotatedParameter<StubParameter>() { _, _ ->
				StubStorageImpl()
			}
		}
		
		val a = injector.get<StubUserManagerImpl>().storage
		
		assertTrue(a is StubStorageImpl)
	}
}