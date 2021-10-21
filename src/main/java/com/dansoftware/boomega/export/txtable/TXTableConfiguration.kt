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

package com.dansoftware.boomega.export.txtable

import com.dansoftware.boomega.database.api.data.RecordProperty
import com.dansoftware.boomega.export.api.RecordExportConfiguration

/**
 * Configuration for the [TXTableExporter]
 */
class TXTableConfiguration : RecordExportConfiguration() {

    override val availableFields: List<RecordProperty<*>>
        get() = AVAILABLE_FIELDS

    /**
     * The vertical alignment for the column-cells
     */
    var headerVerticalAlignment = VerticalAlignment.CENTER

    /**
     * The horizontal alignment for the column-cells
     */
    var headerHorizontalAlignment = HorizontalAlignment.CENTER

    /**
     * The vertical alignment for the regular cells
     */
    var verticalAlignment = VerticalAlignment.CENTER

    /**
     * The horizontal alignment for the regular cells
     */
    var horizontalAlignment = HorizontalAlignment.CENTER

    /**
     * The border style of the table
     */
    var border: Border = Border()

    /**
     * The height of the header row
     */
    var headerHeight = 1

    /**
     * The height of regular rows
     */
    var regularHeight = 1

    /**
     * The minimum width of the header cells
     */
    var headerMinWidth = 10

    /**
     * The minimum width of the regular cells
     */
    var regularMinWidth = 10

    /**
     * The place-holder char to fill the header cell's empty area with
     */
    var headerPlaceHolderChar = ' '

    /**
     * The place-holder char to fill the regular cell's empty area with
     */
    var regularPlaceHolderChar = ' '

    /**
     * The place-holder string to replace null values in the cells
     */
    var nullValuePlaceHolder = "-"

    /**
     * Represents the table's border
     */
    class Border(
        var intersect: Char = '+',
        var horizontal: Char = '-',
        var vertical: Char = '|'
    )

    /**
     * Represents a vertical-alignment
     */
    enum class VerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    /**
     * Represents a horizontal-alignment
     */
    enum class HorizontalAlignment {
        CENTER, LEFT, RIGHT
    }

    companion object {
        private val AVAILABLE_FIELDS by lazy {
            RecordProperty.allProperties - listOf(RecordProperty.SERVICE_CONNECTION, RecordProperty.NOTES)
        }
    }
}