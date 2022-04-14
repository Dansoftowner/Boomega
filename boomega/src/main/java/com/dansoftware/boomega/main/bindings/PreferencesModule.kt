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

import com.dansoftware.boomega.config.source.ConfigSource
import com.dansoftware.boomega.config.source.JsonFileSource
import com.dansoftware.boomega.util.joinToFilePath
import com.dansoftware.boomega.util.userDirectoryPath
import com.google.inject.AbstractModule
import com.google.inject.Provides
import javax.inject.Named

/**
 * DI-module that specifies how to and where to save the configurations.
 */
class PreferencesModule : AbstractModule() {
    override fun configure() {
        bind(ConfigSource::class.java)
            .to(JsonFileSource::class.java)
    }

    @Provides
    @Named("configFilePath")
    @Suppress("unused")
    fun provideConfigFilePath() = joinToFilePath(userDirectoryPath, ".libraryapp2020", "bmcfg")
}