package com.ara.module

interface Module<CONTEXT : Any> {
    fun configure(context: CONTEXT)
}