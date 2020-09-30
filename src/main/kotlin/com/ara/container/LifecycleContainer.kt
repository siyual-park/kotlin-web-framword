package com.ara.container

data class LifecycleContainer<T : Any>(
    val value: T,
    val lifecycle: Lifecycle
)