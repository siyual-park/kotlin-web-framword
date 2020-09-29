package com.ara.container

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Container {
    private val values: ConcurrentHashMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: ConcurrentHashMap<KClass<*>, Provider<*>> = ConcurrentHashMap()

    fun <T: Any, V: T> register(clazz: KClass<T>, value: V): Container {
        values[clazz] = value
        return this
    }

    fun <T: Any, V: T> register(clazz: KClass<T>, provider: Provider<V>): Container {
        providers[clazz] = provider
        return this
    }

    fun <T: Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant find value")
    }

    fun <T: Any> resolveOrNull(clazz: KClass<T>): T? {
        return (values.get(clazz) ?: providers[clazz]?.let { values.putIfAbsent(clazz) { it.get() } }) as T?
    }
}

inline fun <reified T: Any, V: T> Container.register(value: V): Container {
    return this.register(T::class, value)
}
inline fun <reified T: Any, V: T> Container.register(provider: Provider<V>): Container {
    return this.register(T::class, provider)
}
inline fun <reified T: Any> Container.resolve(): T {
    return this.resolve(T::class)
}
inline fun <reified T: Any> Container.resolveOrNull(): T? {
    return this.resolveOrNull(T::class)
}