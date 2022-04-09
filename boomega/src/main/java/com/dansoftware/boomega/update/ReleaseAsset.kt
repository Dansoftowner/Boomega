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

package com.dansoftware.boomega.update

import com.google.gson.annotations.SerializedName
import java.io.InputStream
import java.net.URL

/**
 * Represents an asset (a file) in a particular github release.
 */
open class ReleaseAsset {

    /**
     * The file name
     */
    var name: String? = null

    /**
     * The file size in bytes
     */
    var size: Long = 0

    /**
     * The url to download the file from
     */
    @SerializedName("browser_download_url")
    var downloadUrl: String? = null

    /**
     * The mime-type (or content type) of the file
     */
    @SerializedName("content_type")
    var contentType: String? = null

    /**
     * Opens an input-stream for downloading the asset's file
     */
    open fun openStream(): InputStream = URL(downloadUrl).openStream()

    override fun toString(): String {
        return "GithubAsset(name=$name, size=$size, downloadUrl=$downloadUrl, contentType=$contentType)"
    }

}