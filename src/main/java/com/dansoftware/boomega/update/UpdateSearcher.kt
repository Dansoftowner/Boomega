/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.update

import io.github.g00fy2.versioncompare.Version
import java.util.function.Consumer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Searches for the latest release.
 *
 * @param releasesProvider the object that provides the list of releases
 * @param baseVersion the version the update-searcher should compare the release versions to
 */
open class UpdateSearcher(private val releasesProvider: ReleasesProvider, private val baseVersion: String) {

    /**
     * Searches for the latest release
     *
     * @return the [Release], _null_ if there is no newer release
     */
    open fun search(): Release? {
        val releases: Releases = releasesProvider.getReleases()
        return releases.getOrNull(0)?.takeIf {
            Version(it.version!!.removePrefix("v")).isHigherThan(baseVersion.removePrefix("v"))
        }
    }

    /**
     * Searches for the latest github release without potentially throwing exceptions.
     */
    fun trySearch(onException: Consumer<Exception>? = null): Release? {
        return try {
            search()
        } catch (e: Exception) {
            onException?.accept(e)
            null
        }
    }

    companion object {
        @JvmStatic
        var default by DefaultSearcherDelegate()
    }

    private class DefaultSearcherDelegate : ReadWriteProperty<Companion, UpdateSearcher> {

        private val default by lazy {
            UpdateSearcher(GithubReleasesProvider(GithubRepository("Dansoftowner", "Boomega")), System.getProperty("boomega.version"))
        }

        private var custom: UpdateSearcher? = null

        override fun getValue(thisRef: Companion, property: KProperty<*>): UpdateSearcher {
            return custom ?: default
        }

        @Synchronized
        override fun setValue(thisRef: Companion, property: KProperty<*>, value: UpdateSearcher) {
            custom = value
        }
    }
}