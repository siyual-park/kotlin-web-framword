package com.ara.application

import com.ara.container.ConcurrentContainer
import com.ara.container.Container
import com.ara.container.resolve
import com.ara.module.Module
import com.ara.runner.Runner

class Application<IN : Any, OUT : Any>(
    private val container: Container = ConcurrentContainer.create()
) : Runner<IN, OUT>, Container by container {
    fun use(module: (Container) -> Unit): Application<IN, OUT> {
        return use(object : Module {
            override fun configure(container: Container) {
                module(container)
            }
        })
    }

    fun use(module: Module): Application<IN, OUT> {
        module.configure(this)
        return this
    }

    override fun run(input: IN): OUT {
        val runner: Runner<IN, OUT> = resolve()
        return runner.run(input)
    }
}