package com.ara.runner

interface Runner<IN: Any, OUT: Any> {
    fun run(input: IN): OUT
}