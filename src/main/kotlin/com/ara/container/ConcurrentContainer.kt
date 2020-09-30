package com.ara.container

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

@Suppress("UNCHECKED_CAST")
class ConcurrentContainer : Container {
    private val values: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: MutableMap<KClass<*>, Provider<*>> = ConcurrentHashMap()
    private val classRelations: MutableMap<KClass<*>, MutableSet<KClass<*>>> = ConcurrentHashMap()

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
        calculateRelations(clazz)
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
        calculateRelations(clazz)
        return this
    }

    private fun <T : Any> calculateRelations(clazz: KClass<T>) {
        getOrCreateSubclassSet(clazz)
        clazz.superclasses.forEach {
            getOrCreateSubclassSet(it)
                .add(clazz)
        }
    }

    private fun <T : Any> getOrCreateSubclassSet(clazz: KClass<T>): MutableSet<KClass<*>> {
        val set = classRelations.getOrPut(clazz) { Collections.newSetFromMap(ConcurrentHashMap()) }
        set.add(clazz)
        return set
    }


    override fun <T : Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> resolveOrNull(clazz: KClass<T>): T? {
        return classRelations[clazz]?.let { subclasses ->
            for (subclass in subclasses) {
                val value = exactlyResolveOrNull(subclass)
                if (value != null) {
                    return@let value
                }
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