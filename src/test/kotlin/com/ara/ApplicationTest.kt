package com.ara

import com.ara.application.Application
import com.ara.container.registerProvider
import com.ara.container.resolve
import com.ara.mock.Controller
import com.ara.mock.Service
import com.ara.runner.Runner
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun test() {
        val application = Application<Array<Int>, Int>()
        application.use {
            it.registerProvider { Service() }
            it.registerProvider { Controller(resolve()) }
            it.registerProvider<Runner<Array<Int>, Int>> {
                object : Runner<Array<Int>, Int> {
                    private val controller: Controller = resolve()

                    override fun run(input: Array<Int>): Int {
                        return controller.add(input)
                    }
                }
            }
        }

        val result = application.run(arrayOf(1, 2, 3, 4, 5))
        assertEquals(result, 15)
    }
}