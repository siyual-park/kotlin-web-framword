package com.ara.container

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ConcurrentContainer : Container {
    private val values: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: MutableMap<KClass<*>, Provider<*>> = ConcurrentHashMap()

    override val size: Int
        get() = values.size

    override val providerSize: Int
        get() = providers.size

    override fun <T : Any> unregisterAll(clazz: KClass<T>): Container {
        unregister(clazz)
        unregisterProvider(clazz)
        return this
    }

    override fun <T : Any> unregister(clazz: KClass<T>): Container {
        values.remove(clazz)
        return this
    }

    override fun <T : Any> unregisterProvider(clazz: KClass<T>): Container {
        providers.remove(clazz)
        return this
    }

    override fun <T : Any> register(clazz: KClass<T>, value: T): Container {
        values[clazz] = value
        return this
    }

    override fun <T : Any> registerProvider(clazz: KClass<T>, provider: Container.() -> T): Container {
        val container = this
        return registerProvider(clazz, object : Provider<T> {
            override fun get(): T {
                return provider(container)
            }
        })
    }

    override fun <T : Any> registerProvider(clazz: KClass<T>, provider: Provider<in T>): Container {
        providers[clazz] = provider
        return this
    }

    override fun <T : Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> resolveOrNull(clazz: KClass<T>): T? {
        val value = values[clazz]
        if (value != null) {
            return value as T
        }
        val provider = providers[clazz]
        if (provider != null) {
            val newValue = provider.get()
            values.putIfAbsent(clazz, provider.get())
            return newValue as T
        }
        return null
    }

    companion object {
        fun create(): ConcurrentContainer {
            return ConcurrentContainer()
        }
    }
}