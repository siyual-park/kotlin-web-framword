package com.ara

import com.ara.container.*
import com.ara.mock.Controller
import com.ara.mock.Service
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContainerTest {
    @Test
    fun test() {
        val container = ConcurrentContainer.create()
        container.registerProvider { Service() }
        container.registerProvider { Controller(resolve()) }

        assertEquals(container.size, 0)
        assertEquals(container.providerSize, 2)

        val controller: Controller? = container.resolveOrNull()

        assertNotNull(controller)
        assertEquals(container.size, 2)
        assertEquals(container.providerSize, 2)
    }
}