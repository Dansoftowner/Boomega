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

import com.dansoftware.boomega.export.api.RecordExportConfiguration

class TXTableConfiguration : RecordExportConfiguration() {

    var headerVerticalAlignment = VerticalAlignment.CENTER

    var headerHorizontalAlignment = HorizontalAlignment.CENTER

    var verticalAlignment = VerticalAlignment.CENTER

    var horizontalAlignment = HorizontalAlignment.CENTER

    var border: Border? = Border()

    var headerHeight = 1

    var regularHeight = 1

    class Border(
        val intersect: Char = '+',
        val horizontal: Char = '-',
        val vertical: Char = '|'
    )

    enum class VerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    enum class HorizontalAlignment {
        CENTER, LEFT, RIGHT
    }
}