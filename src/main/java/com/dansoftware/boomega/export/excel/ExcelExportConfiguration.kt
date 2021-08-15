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

package com.dansoftware.boomega.export.excel

import com.dansoftware.boomega.export.api.RecordExportConfiguration

/**
 * A [ExcelExportConfiguration] allows to specify configurations for an [ExcelExporter].
 */
class ExcelExportConfiguration : RecordExportConfiguration() {

    /**
     * The name of the Excel sheet where the records will be exported
     */
    var sheetName: String? = null

    /**
     * The style of the header row cells
     */
    var headerCellStyle = CellStyle(isBold = true)

    /**
     * The style of the cells of the regular rows
     */
    var regularCellStyle = CellStyle()

    /**
     * Holds the configurable cell attributes/styles.
     */
    class CellStyle(
        var fontName: String? = null,
        var isBold: Boolean = false,
        var isItalic: Boolean = false,
        var isStrikeout: Boolean = false,
        var underline: Byte = 0,
        var fontColor: java.awt.Color? = null,
        var backgroundColor: java.awt.Color? = null
    )
}