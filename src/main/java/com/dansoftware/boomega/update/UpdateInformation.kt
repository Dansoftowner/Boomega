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

import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * This class responsible for storing the information about
 * an update
 *
 * @author Daniel Gyorffy
 */
open class UpdateInformation

/**
 * This constructor creates an UpdateInformationObject with the required values
 *
 * @param version   the new version of the update
 * @param reviewUrl the location of the review markdown-text.
 * @param binaries  the List that contains the object representations of the downloadable binaries
 */
constructor(val version: String, val reviewUrl: String, val binaries: List<DownloadableBinary>) {
    open fun reviewReader(): Reader = InputStreamReader(URL(reviewUrl).openStream(), StandardCharsets.UTF_8)
}