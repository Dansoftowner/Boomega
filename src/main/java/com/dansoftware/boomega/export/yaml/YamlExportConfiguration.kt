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

package com.dansoftware.boomega.export.yaml

import com.dansoftware.boomega.export.api.RecordExportConfiguration

class YamlExportConfiguration : RecordExportConfiguration() {

    var defaultScalarStyle: ScalarStyle = ScalarStyle.PLAIN

    var defaultFlowStyle: FlowStyle = FlowStyle.AUTO

    var isCanonical = false

    var isAllowUnicode = true

    var indent = 2

    var bestWidth = 80

    var splitLines = true

    var explicitStart = false

    var explicitEnd = false

    var prettyFlow = false

    enum class ScalarStyle(val displayName: String) {
        // TODO: i18n
        DOUBLE_QUOTED("Double quoted"),
        SINGLE_QUOTED("Single quoted"),
        LITERAL("Literal"),
        FOLDED("Folded"),
        PLAIN("Plain")
    }

    enum class FlowStyle(val displayName: String) {
        // TODO: i18n
        FLOW("flow"),
        BLOCK("block"),
        AUTO("auto")
    }
}