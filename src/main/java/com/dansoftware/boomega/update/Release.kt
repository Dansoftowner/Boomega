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

import com.google.gson.annotations.SerializedName

/**
 * Represents a Github release
 */
class Release {

    /**
     * The website where this release can be viewed
     */
    @SerializedName("html_url")
    var website: String? = null

    /**
     * The version of the release (tag name)
     */
    @SerializedName("tag_name")
    var version: String? = null

    /**
     * Determines whether the release is a pre-release or a production-ready release
     */
    @SerializedName("prerelease")
    var isPrerelease: Boolean = false

    /**
     * The description of the release
     */
    @SerializedName("body")
    var description: String? = null

    /**
     * The assets of this release
     */
    var assets: List<ReleaseAsset>? = null

    //var publishedDate: LocalDate
}