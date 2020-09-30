package com.ara.container

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

@Suppress("UNCHECKED_CAST")
class ConcurrentContainer : Container {
    private val values: MutableMap<KClass<*>, Any> = ConcurrentHashMap()
    private val providers: MutableMap<KClass<*>, Provider<*>> = ConcurrentHashMap()
    private val classRelations: MutableMap<KClass<*>, MutableSet<ClassRelation<*>>> = ConcurrentHashMap()

    override val size: Int
        get() = values.size

    override val providerSize: Int
        get() = providers.size

    override fun <T : Any> unregisterAll(clazz: KClass<T>): Container {
        values.remove(clazz)
        providers.remove(clazz)
        unregisterClassRelations(clazz)
        return this
    }

    override fun <T : Any> unregister(clazz: KClass<T>): Container {
        values.remove(clazz)
        unregisterClassRelations(clazz)
        return this
    }

    override fun <T : Any> unregisterProvider(clazz: KClass<T>): Container {
        providers.remove(clazz)
        unregisterClassRelations(clazz)
        return this
    }

    override fun <T : Any, V : T> register(clazz: KClass<T>, value: V): Container {
        values[clazz] = value
        registerClassRelations(clazz)
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
        registerClassRelations(clazz)
        return this
    }

    private fun <T : Any> unregisterClassRelations(clazz: KClass<T>) {
        classRelations.forEach { (_, classRelations) ->
            classRelations.removeIf { it.clazz === clazz }
        }
    }

    private fun <T : Any> registerClassRelations(clazz: KClass<T>) {
        getOrCreateClassRelations(clazz)
        clazz.superclasses.forEachIndexed { i, superClass ->
            getOrCreateClassRelations(superClass)
                .add(ClassRelation(clazz, i + 1))
        }
    }

    private fun <T : Any> getOrCreateClassRelations(clazz: KClass<T>): MutableSet<ClassRelation<*>> {
        val set = classRelations.getOrPut(clazz) {
            Collections.synchronizedSortedSet(TreeSet { a, b ->
                a.diff - b.diff
            })
        }
        set.add(ClassRelation(clazz, 0))
        return set
    }


    override fun <T : Any> resolve(clazz: KClass<T>): T {
        return resolveOrNull(clazz) ?: throw RuntimeException("Cant resolve $clazz")
    }

    override fun <T : Any> resolveOrNull(clazz: KClass<T>): T? {
        return classRelations[clazz]?.let { classRelations ->
            for ((subclass) in classRelations) {
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