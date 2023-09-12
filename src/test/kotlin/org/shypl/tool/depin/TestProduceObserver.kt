package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestProduceObserver {
	@Test
	fun before_produce() {
		var observed = false
		
		val injector = Injector {
			addProduceObserverBefore<StubAutoImplementation> {
				observed = true
			}
		}
		
		injector.get<StubAutoImplementation>()
		
		assertTrue(observed)
	}
	
	@Test
	fun before_after() {
		var observed: StubAutoImplementation? = null
		
		val injector = Injector {
			addProduceObserverAfter<StubAutoImplementation> { _, obj ->
				observed = obj
			}
		}
		
		val obj = injector.get<StubAutoImplementation>()
		
		assertSame(obj, observed)
	}
	
}