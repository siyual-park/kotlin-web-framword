package com.ara.container

import kotlin.reflect.KClass

data class ClassRelation<T: Any>(
    val clazz: KClass<T>,
    val diff: Int
)