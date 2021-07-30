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

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.export.ConfigurationPanel
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.concurrent.Task

interface RecordExporter<C : RecordExportConfiguration> {

    val name: String
    val icon: MaterialDesignIcon
    val configurationPanel: ConfigurationPanel<C>
    val contentType: String
    val contentTypeDescription: String

    fun getTask(items: List<Record>, config: C): Task<Unit>

}