package com.ara.container

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

@Suppress("UNCHECKED_CAST")
class ConcurrentContainer : Container {
    private val values: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: MutableMap<KClass<*>, Provider<*>> = ConcurrentHashMap()
    private val subclassRelations: MutableMap<KClass<*>, MutableSet<KClass<*>>> = ConcurrentHashMap()

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

    override fun <T : Any, V : T> register(clazz: KClass<T>, value: V): Container {
        values[clazz] = value
        registerClass(clazz)
        return this
    }

    override fun <T : Any, V : T> registerProvider(clazz: KClass<T>, provider: Container.() -> V): Container {
        val container = this
        return registerProvider(clazz, object : Provider<V> {
            override fun get(): V {
                return provider(container)
            }
        })
    }

    override fun <T : Any, V : T> registerProvider(clazz: KClass<T>, provider: Provider<V>): Container {
        providers[clazz] = provider
        registerClass(clazz)
        return this
    }

    private fun <T : Any> registerClass(clazz: KClass<T>) {
        subclassRelations.getOrPut(clazz) { Collections.newSetFromMap(ConcurrentHashMap()) }
            .add(clazz)

        clazz.superclasses.forEach {
            subclassRelations.getOrPut(it) { Collections.newSetFromMap(ConcurrentHashMap()) }
                .add(clazz)
        }
    }

    override fun <T : Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> resolveOrNull(clazz: KClass<T>): T? {
        return subclassRelations[clazz]?.let { subclasses ->
            for (subclass in subclasses) {
                return@let exactlyResolve(subclass)
            }
            return@let null
        } as T?
    }

    override fun <T : Any> exactlyResolve(clazz: KClass<T>): T {
        return exactlyResolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> exactlyResolveOrNull(clazz: KClass<T>): T? {
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