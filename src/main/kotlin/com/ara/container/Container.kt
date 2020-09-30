package com.ara.container

import kotlin.reflect.KClass

interface Container {
    val size: Int
    val providerSize: Int

    fun <T: Any> unregisterAll(clazz: KClass<T>): Container

    fun <T: Any> unregister(clazz: KClass<T>): Container

    fun <T: Any> unregisterProvider(clazz: KClass<T>): Container

    fun <T : Any> register(clazz: KClass<T>, value: T): Container

    fun <T : Any> registerProvider(clazz: KClass<T>, provider: Container.() -> T): Container

    fun <T : Any> registerProvider(clazz: KClass<T>, provider: Provider<in T>): Container

    fun <T: Any> resolve(clazz: KClass<T>): T

    fun <T: Any> resolveOrNull(clazz: KClass<T>): T?
}

inline fun <reified T : Any> Container.register(value: T): Container {
    return this.register(T::class, value)
}

inline fun <reified T : Any> Container.registerProvider(noinline provider: Container.() -> T): Container {
    return this.registerProvider(T::class, provider)
}

inline fun <reified T : Any> Container.registerProvider(provider: Provider<in T>): Container {
    return this.registerProvider(T::class, provider)
}

inline fun <reified T : Any> Container.resolve(): T {
    return this.resolve(T::class)
}
inline fun <reified T: Any> Container.resolveOrNull(): T? {
    return this.resolveOrNull(T::class)
}