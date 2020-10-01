package com.ara.application

import com.ara.container.ConcurrentContainer
import com.ara.container.Container
import com.ara.container.resolve
import com.ara.runner.Runner

class SimpleApplication<IN : Any, OUT : Any>(
        context: Container = ConcurrentContainer()
) : Application<IN, OUT, Container>(context) {
    override fun run(input: IN): OUT {
        val runner: Runner<IN, OUT> = context.resolve()
        return runner.run(input)
    }
}