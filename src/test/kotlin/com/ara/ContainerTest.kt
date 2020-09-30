package com.ara

import com.ara.container.ConcurrentContainer
import com.ara.container.resolve
import com.ara.container.resolveOrNull
import com.ara.container.single
import com.ara.mock.Controller
import com.ara.mock.Service
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ContainerTest {
    @Test
    fun testResolve() {
        val container = ConcurrentContainer.create()
        container.single { Service() }
        container.single { Controller(resolve()) }

        val controller: Controller? = container.resolveOrNull()

        assertNotNull(controller)
    }
}