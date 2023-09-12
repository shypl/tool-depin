package org.shypl.tool.depin

import kotlin.test.Test
import kotlin.test.assertFailsWith

class TestCircularDependency {
	@Test
	fun test() {
		val injector = Injector {}
		
		assertFailsWith<InjectException>{
			injector.get<StubCircularA>()
		}
	}
}