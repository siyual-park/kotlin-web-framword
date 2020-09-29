package com.ara.application

import com.ara.container.Container
import com.ara.container.resolve
import com.ara.module.Module
import com.ara.runner.Runner

class Application : Runner {
    private val container: Container = Container()

    fun use(module: Module): Application {
        module.configure(container)
        return this
    }

    override fun run(args: Array<String>) {
        val runner: Runner = container.resolve()
        runner.run(args)
    }
}