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
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Searches for the latest release.
 *
 * @param releasesProvider the object that provides the list of releases
 * @param baseVersion the version the update-searcher should compare the release versions to
 */
@Singleton
open class UpdateSearcher @Inject constructor(
    private val releasesProvider: ReleasesProvider,
    @param:Named("appVersion") private val baseVersion: String
) {

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
}