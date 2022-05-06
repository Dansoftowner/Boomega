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

package com.dansoftware.boomega.export.json

import com.dansoftware.boomega.export.api.RecordExportAPI
import com.dansoftware.boomega.export.api.RecordExportConfiguration

/**
 * A [JsonExportConfiguration] allows to specify configurations for a [JsonExporter].
 */
@OptIn(RecordExportAPI::class)
class JsonExportConfiguration : RecordExportConfiguration() {

    /**
     * Configures the exporter whether it should output Json formatted prettily or not.
     *
     * By default, it's _true_.
     */
    var prettyPrinting = true

    /**
     * Configures the exporter whether it should make the output JSON non-executable
     * in Javascript by prefixing it with some special text or not.
     *
     * By default, it's _false_.
     */
    var nonExecutableJson = false

    /**
     * Configures the exporter whether it should serialize null fields or not.
     *
     * By default, the exporter omits all fields that are null during serialization.
     */
    var serializeNulls = false
}