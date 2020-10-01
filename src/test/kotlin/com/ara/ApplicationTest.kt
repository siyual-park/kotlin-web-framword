package com.ara

import com.ara.application.SimpleApplication
import com.ara.application.install
import com.ara.container.inject
import com.ara.container.resolve
import com.ara.container.single
import com.ara.mock.Controller
import com.ara.mock.Service
import com.ara.runner.Runner
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun test() {
        val application = SimpleApplication<Array<Int>, Int>()
        application.install {
            single { Service() }
            single { Controller(resolve()) }
            single<Runner<Array<Int>, Int>> {
                object : Runner<Array<Int>, Int> {
                    private val controller: Controller by inject()

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