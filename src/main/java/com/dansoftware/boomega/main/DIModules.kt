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

package com.dansoftware.boomega.main

import com.dansoftware.boomega.config.source.ConfigSource
import com.dansoftware.boomega.config.source.JsonFileSource
import com.dansoftware.boomega.update.GithubReleasesProvider
import com.dansoftware.boomega.update.ReleasesProvider
import com.dansoftware.boomega.util.joinToFilePath
import com.dansoftware.boomega.util.userDirectoryPath
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Names
import javax.inject.Named

class PreferencesModule : AbstractModule() {
    override fun configure() {
        bind(ConfigSource::class.java).to(JsonFileSource::class.java)
    }

    @Provides
    @Named("configFilePath")
    fun provideConfigFilePath() = joinToFilePath(userDirectoryPath, ".libraryapp2020", "bmcfg")
}

class UpdateModule : AbstractModule() {
    override fun configure() {
        bind(ReleasesProvider::class.java).to(GithubReleasesProvider::class.java)
        bind(String::class.java)
            .annotatedWith(Names.named("GithubRepositoryOwner"))
            .toInstance("Dansoftowner")
        bind(String::class.java)
            .annotatedWith(Names.named("GithubRepositoryName"))
            .toInstance("Boomega")
    }

    @Provides
    @Named("appVersion")
    fun provideAppVersion() = System.getProperty("app.version")
}