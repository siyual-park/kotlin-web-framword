package com.ara.container

import kotlin.reflect.KClass

interface Container {
    fun <T : Any> unregister(clazz: KClass<T>): Container

    fun <T : Any> register(clazz: KClass<T>, lifecycle: Lifecycle, provider: Provider<in T>): Container

    fun <T : Any> resolve(clazz: KClass<T>): T

    fun <T : Any> resolveOrNull(clazz: KClass<T>): T?
}

inline fun <reified T : Any> Container.factory(noinline provider: Container.() -> T): Container {
    return this.factory(T::class, provider)
}

inline fun <reified T : Any> Container.factory(clazz: KClass<T>, noinline provider: Container.() -> T): Container {
    return this.register(clazz, Lifecycle.PreRequest, provider)
}

inline fun <reified T : Any> Container.single(noinline provider: Container.() -> T): Container {
    return this.single(T::class, provider)
}

inline fun <reified T : Any> Container.single(clazz: KClass<T>, noinline provider: Container.() -> T): Container {
    return this.register(clazz, Lifecycle.Singleton, provider)
}

inline fun <reified T : Any> Container.register(lifecycle: Lifecycle, noinline provider: Container.() -> T): Container {
    return this.register(T::class, lifecycle, provider)
}

inline fun <reified T : Any> Container.register(lifecycle: Lifecycle, provider: Provider<in T>): Container {
    return this.register(T::class, lifecycle, provider)
}

inline fun <T : Any> Container.register(
    clazz: KClass<T>,
    lifecycle: Lifecycle,
    crossinline provider: Container.() -> T
): Container {
    val container = this
    return this.register(clazz, lifecycle, object : Provider<T> {
        override fun get(): T {
            return provider(container)
        }
    })
}

inline fun <reified T : Any> Container.resolve(): T {
    return this.resolve(T::class)
}

inline fun <reified T : Any> Container.resolveOrNull(): T? {
    return this.resolveOrNull(T::class)
}