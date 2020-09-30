package com.ara.container

import kotlin.reflect.KClass

interface Container {
    val size: Int
    val providerSize: Int

    fun <T: Any> unregister(clazz: KClass<T>): Container

    fun <T : Any> register(clazz: KClass<T>, provider: Container.() -> T): Container

    fun <T : Any> register(clazz: KClass<T>, provider: Provider<in T>): Container

    fun <T: Any> resolve(clazz: KClass<T>): T

    fun <T: Any> resolveOrNull(clazz: KClass<T>): T?
}

inline fun <reified T : Any> Container.register(noinline provider: Container.() -> T): Container {
    return this.register(T::class, provider)
}

inline fun <reified T : Any> Container.register(provider: Provider<in T>): Container {
    return this.register(T::class, provider)
}

inline fun <reified T : Any> Container.resolve(): T {
    return this.resolve(T::class)
}
inline fun <reified T: Any> Container.resolveOrNull(): T? {
    return this.resolveOrNull(T::class)
}