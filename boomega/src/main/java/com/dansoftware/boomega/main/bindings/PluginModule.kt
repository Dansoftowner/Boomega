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

package com.dansoftware.boomega.main.bindings

import com.dansoftware.boomega.plugin.RealtimePluginService
import com.dansoftware.boomega.plugin.api.PluginService
import com.google.inject.AbstractModule
import com.google.inject.Provides
import java.io.File
import javax.inject.Named

/**
 * DI-module that provides the default plugin-directory and the [PluginService] implementation.
 */
class PluginModule : AbstractModule() {
    override fun configure() {
        bind(PluginService::class.java).to(RealtimePluginService::class.java)
    }

    @Provides
    @Named("jarDirectory")
    @Suppress("unused")
    fun providePluginDirectory() = File(System.getProperty("boomega.plugin.dir"))
}