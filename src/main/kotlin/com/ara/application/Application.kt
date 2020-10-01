package com.ara.application

import com.ara.module.Module
import com.ara.runner.Runner

abstract class Application<IN : Any, OUT : Any, CONTEXT : Any>(
        protected val context: CONTEXT
) : Runner<IN, OUT> {
    fun install(module: Module<CONTEXT>): Application<IN, OUT, CONTEXT> {
        module.configure(context)
        return this
    }
}

fun <IN : Any, OUT : Any, CONTEXT : Any> Application<IN, OUT, CONTEXT>.install(module: CONTEXT.() -> Unit): Application<IN, OUT, CONTEXT> {
    return install(object : Module<CONTEXT> {
        override fun configure(context: CONTEXT) {
            module(context)
        }
    })
}
