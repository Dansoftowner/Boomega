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

package com.dansoftware.boomega.export.api

import com.dansoftware.boomega.db.data.RecordProperty
import java.io.OutputStream
import java.util.*

/**
 * A [RecordExportConfiguration] allows to specify configurations
 * for a particular [RecordExporter].
 */
abstract class RecordExportConfiguration {

    /**
     * The [OutputStream] the exporter should write the result to.
     *
     * By default, it's a *null output stream*.
     *
     * @see OutputStream.nullOutputStream
     */
    var outputStream: OutputStream = OutputStream.nullOutputStream()

    /**
     * The required record-fields (represented by [RecordProperty] objects) to be handled
     * by the exporter.
     *
     * By default, all properties are handled.
     */
    var requiredFields: List<RecordProperty<*>> = RecordProperty.allProperties

    /**
     * The record-field (represented by a [RecordProperty]) to be used by
     * the exporter for sorting the records.
     *
     * If it's null (as by default), no sorting will be performed.
     */
    var fieldToSortBy: RecordProperty<Comparable<*>>? = null

    /**
     * The [Locale] representing the abc the exporter should use in case of
     * sorting records.
     * It's only used for sorting **string**-properties.
     *
     * Note: if the [fieldToSortBy] is not specified, this has no effect.
     */
    var sortingAbc: Locale = Locale.forLanguageTag("")

    /**
     * Specifies if the order of the records should be reversed.
     */
    var reverseItems: Boolean = false
}