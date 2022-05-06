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

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.i18n.api.I18N
@RecordExportAPI
abstract class BaseExporter<C : RecordExportConfiguration> : RecordExporter<C> {
    protected fun sortRecords(items: List<Record>, config: C): List<Record> {
        val sortedItems = config.fieldToSortBy?.let { field ->
            val abcCollator = I18N.getABCCollator(config.sortingAbc).orElse(null)
            @Suppress("UNCHECKED_CAST")
            items.sortedWith { o1, o2 ->
                val o1Value = field.getValue(o1)
                val o2Value = field.getValue(o2)

                if ((o1Value is String || o2Value is String) && abcCollator != null)
                    abcCollator.compare(o1Value, o2Value)
                else o2Value?.let { (o1Value as? Comparable<Comparable<*>>)?.compareTo(it) } ?: 0
            }
        } ?: items
        return if (config.reverseItems) sortedItems.asReversed() else sortedItems
    }
}