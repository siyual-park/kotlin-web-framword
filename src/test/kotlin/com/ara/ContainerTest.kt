package com.ara

import com.ara.container.ConcurrentContainer
import com.ara.container.register
import com.ara.container.resolve
import com.ara.container.resolveOrNull
import com.ara.mock.Controller
import com.ara.mock.Service
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContainerTest {
    @Test
    fun testRegister() {
        val container = ConcurrentContainer.create()
        container.register { Service() }
        container.register { Controller(resolve()) }

        assertEquals(container.size, 0)
        assertEquals(container.providerSize, 2)
    }

    @Test
    fun testResolve() {
        val container = ConcurrentContainer.create()
        container.register { Service() }
        container.register { Controller(resolve()) }

        val controller: Controller? = container.resolveOrNull()

        assertNotNull(controller)
        assertEquals(container.size, 2)
        assertEquals(container.providerSize, 2)
    }

    @Test
    fun testUnRegister() {
        val container = ConcurrentContainer.create()
        container.register { Service() }
        container.register { Controller(resolve()) }

        container.unregisterAll(Service::class)
        container.unregisterAll(Controller::class)

        assertEquals(container.size, 0)
        assertEquals(container.providerSize, 0)
    }
}