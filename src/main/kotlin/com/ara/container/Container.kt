package com.ara.container

import kotlin.reflect.KClass

interface Container {
    val size: Int
    val providerSize: Int

    fun <T: Any> unregisterAll(clazz: KClass<T>): Container

    fun <T: Any> unregister(clazz: KClass<T>): Container

    fun <T: Any> unregisterProvider(clazz: KClass<T>): Container

    fun <T: Any, V: T> register(clazz: KClass<T>, value: V): Container

    fun <T: Any, V: T> registerProvider(clazz: KClass<T>, provider: Container.() -> V): Container

    fun <T: Any, V: T> registerProvider(clazz: KClass<T>, provider: Provider<V>): Container

    fun <T: Any> resolve(clazz: KClass<T>): T

    fun <T: Any> resolveOrNull(clazz: KClass<T>): T?

    fun <T: Any> exactlyResolve(clazz: KClass<T>): T

    fun <T: Any> exactlyResolveOrNull(clazz: KClass<T>): T?
}

inline fun <reified T: Any, V: T> Container.register(value: V): Container {
    return this.register(T::class, value)
}
inline fun <reified T: Any, V: T> Container.registerProvider(noinline provider: Container.() -> V): Container {
    return this.registerProvider(T::class, provider)
}
inline fun <reified T: Any, V: T> Container.registerProvider(provider: Provider<V>): Container {
    return this.registerProvider(T::class, provider)
}
inline fun <reified T: Any> Container.resolve(): T {
    return this.resolve(T::class)
}
inline fun <reified T: Any> Container.resolveOrNull(): T? {
    return this.resolveOrNull(T::class)
}
inline fun <reified T: Any> Container.exactlyResolve(): T {
    return this.exactlyResolve(T::class)
}
inline fun <reified T: Any> Container.exactlyResolveOrNull(): T? {
    return this.exactlyResolveOrNull(T::class)
}