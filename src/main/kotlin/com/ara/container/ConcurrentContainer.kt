package com.ara.container

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ConcurrentContainer : Container {
    private val singletons: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: MutableMap<KClass<*>, LifecycleContainer<Provider<*>>> = ConcurrentHashMap()

    override fun <T : Any> unregister(clazz: KClass<T>): Container {
        singletons.remove(clazz)
        providers.remove(clazz)
        return this
    }

    override fun <T : Any> register(clazz: KClass<T>, lifecycle: Lifecycle, provider: Provider<in T>): Container {
        providers[clazz] = LifecycleContainer(provider, lifecycle)
        return this
    }

    override fun <T : Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> resolveOrNull(clazz: KClass<T>): T? {
        return providers[clazz]?.let { (provider, lifecycle) ->
            when (lifecycle) {
                Lifecycle.PreRequest -> provider.get()
                Lifecycle.Singleton -> singletons.getOrPut(clazz) { provider.get() }
            }
        } as T?
    }

    companion object {
        fun create(): ConcurrentContainer {
            return ConcurrentContainer()
        }
    }
}