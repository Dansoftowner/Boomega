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

@file:JvmName("VolumeUtils")

package com.dansoftware.boomega.service.googlebooks

import com.dansoftware.boomega.util.parseToDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

private val logger: Logger = LoggerFactory.getLogger("Volumes")

val Volume.VolumeInfo.isMagazine: Boolean
    get() = (printType == Volume.VolumeInfo.MAGAZINE)

val Volume.VolumeInfo.isBook: Boolean
    get() = (printType == Volume.VolumeInfo.BOOK)

fun Volume.VolumeInfo.getPublishedDateObject(): LocalDate? =
    publishedDate?.parseToDate("yyyy-MM", "yyyy") { format, e ->
        logger.error("Couldn't parse date with format '{}'", format, e)
    }

fun Volume.VolumeInfo.getIndustryIdentifiersAsString() =
    industryIdentifiers?.joinToString("\n")
