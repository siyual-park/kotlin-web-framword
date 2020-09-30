package com.ara.mock

class Controller(
    private val service: Service
) {
    fun add(values: Array<Int>): Int {
        return values.reduce { acc, i -> service.add(acc, i) }
    }
}