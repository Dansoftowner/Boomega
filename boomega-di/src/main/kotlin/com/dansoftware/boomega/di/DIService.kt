/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.di

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.name.Names
import kotlin.reflect.KClass

/**
 * The global wrapper of the Guice Dependency Injection framework.
 */
object DIService {

    @Volatile
    private var modules: List<Module>? = null

    /**
     * The guice injector used for constructing objects.
     *
     * Not available if the Guice [Module]s were not initialized
     * by the [initModules] method.
     */
    lateinit var injector: Injector
        private set

    /**
     * Creates the [injector] with the given guice modules.
     *
     * It must be called only once during the application runtime!
     *
     * @throws [IllegalStateException] if the modules are already specified
     */
    @Synchronized
    @JvmStatic
    fun init(vararg modules: Module) {
        check(DIService.modules == null) { "Modules has been specified once" }
        DIService.modules = listOf(*modules)
        injector = Guice.createInjector(*modules)
    }

    /**
     * @see init
     */
    @Deprecated(
        "Use the init() method instead",
        ReplaceWith("DIService.init(*modules)", "com.dansoftware.boomega.di.DIService")
    )
    @JvmStatic
    fun initModules(vararg modules: Module) {
        init(*modules)
    }


    /**
     * Constructs an object for the given type.
     *
     * @param type the [java.lang.Class] representing the type
     */
    @JvmStatic
    fun <T> get(type: Class<T>): T = injector.getInstance(type)

    /**
     * Constructs an object for the given type.
     *
     * @param type the [kotlin.reflect.KClass] representing the type
     */
    @JvmStatic
    fun <T : Any> get(type: KClass<T>): T = get(type.java)

    /**
     * Constructs an object for the given type and name.
     *
     * @param type the [java.lang.Class] representing the type
     * @param name the name the class is annotated with (see: [javax.inject.Named])
     */
    @JvmStatic
    fun <T> get(type: Class<T>, name: String): T = injector.getInstance(Key.get(type, Names.named(name)))

    /**
     * Constructs an object for the given type and name.
     *
     * @param type the [kotlin.reflect.KClass] representing the type
     * @param name the name the class is annotated with (see: [javax.inject.Named])
     */
    @JvmStatic
    fun <T : Any> get(type: KClass<T>, name: String): T = get(type.java, name)
}