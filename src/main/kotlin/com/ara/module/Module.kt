package com.ara.module

import com.ara.container.Container

interface Module {
    fun configure(container: Container)
}