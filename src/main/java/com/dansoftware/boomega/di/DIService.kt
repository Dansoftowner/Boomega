/*
 * Boomega
 * Copyright (c) 2020-2022  Daniel Gyoerffy
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

object DIService {
    private var modules: List<Module>? = null

    lateinit var injector: Injector
        private set

    @Synchronized
    @JvmStatic
    fun initModules(vararg modules: Module) {
        check(this.modules == null) { "Modules has been specified once" }
        this.modules = listOf(*modules)
        injector = Guice.createInjector(*modules)
    }

    @JvmStatic
    fun <T> get(type: Class<T>): T = injector.getInstance(type)

    @JvmStatic
    fun <T : Any> get(type: KClass<T>): T = get(type.java)

    @JvmStatic
    fun <T> get(type: Class<T>, name: String): T = injector.getInstance(Key.get(type, Names.named(name)))

    @JvmStatic
    fun <T : Any> get(type: KClass<T>, name: String): T = get(type.java, name)
}