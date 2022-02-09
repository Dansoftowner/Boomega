/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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
import com.google.inject.Module

object DIService {
    private var modules: List<Module>? = null

    lateinit var injector: Injector
        private set

    @Synchronized
    fun initModules(vararg modules: Module) {
        check(this.modules == null) { "Modules has been specified once" }
        this.modules = listOf(*modules)
        injector = Guice.createInjector(*modules)
    }

    operator fun <T> get(type: Class<T>): T =
        injector.getInstance(type)
}