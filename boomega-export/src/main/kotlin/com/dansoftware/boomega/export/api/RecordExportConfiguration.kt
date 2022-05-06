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

package com.dansoftware.boomega.export.api

import com.dansoftware.boomega.database.api.data.RecordProperty
import java.util.*

/**
 * A [RecordExportConfiguration] allows to specify configurations
 * for a particular [RecordExporter].
 */
@RecordExportAPI
abstract class RecordExportConfiguration {

    /**
     * The available record-fields (represented by [RecordProperty] objects) that can be exported by the exporter.
     */
    open val availableFields: List<RecordProperty<*>>
        get() = RecordProperty.allProperties

    /**
     * The required record-fields (represented by [RecordProperty] objects) to be handled
     * by the exporter.
     *
     * By default, all properties are handled.
     */
    var requiredFields: List<RecordProperty<*>> = availableFields

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