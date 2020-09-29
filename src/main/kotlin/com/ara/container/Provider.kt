package com.ara.container

interface Provider<T: Any> {
    fun get(): T
}