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
import com.dansoftware.boomega.gui.export.ConfigurationDialog
import javafx.concurrent.Task
import javafx.scene.Node
import java.io.OutputStream

/**
 * A [RecordExporter] allows to export [Record]s into a particular format.
 *
 * @param C specifies the [RecordExportConfiguration] the exporter requires
 */
interface RecordExporter<C : RecordExportConfiguration> {

    /**
     * The name of the format this exporter will export the records to
     */
    val name: String

    /**
     * The graphical icon for the exporter
     */
    val icon: Node

    /**
     * The [ConfigurationDialog] the exporter provides
     */
    val configurationDialog: ConfigurationDialog<C>

    /**
     * The mime-type (or content-type) of the format.
     * It's usually identical with the file-extension.
     */
    val contentType: String

    /**
     * A simple description of the content-type
     */
    val contentTypeDescription: String

    /**
     * Gives `false` if the exporter shouldn't be used yet
     */
    val isDisabled: Boolean get() = false

    /**
     * Exports the given records.
     *
     * @param items the records the exporter should export
     * @param output the [OutputStream] the exporter should write the result to.
     * @param config the configuration-object required by the exporter
     * @param observer the observer for handling the progress, messages etc...
     */
    fun write(items: List<Record>, output: OutputStream, config: C, observer: ExportProcessObserver)

    /**
     * Builds a [Task] for the exporting-process
     *
     * @param items the list of records the task should export
     * @param config the configuration-object required by the exporter
     */
    fun task(items: List<Record>, out: OutputStream, config: C): Task<Unit> =
        object : Task<Unit>() {
            override fun call() {
                let { taskObj ->
                    write(items, out, config, object : ExportProcessObserver {
                        override fun updateMessage(message: String?) = taskObj.updateMessage(message)
                        override fun updateProgress(workDone: Double, max: Double) = taskObj.updateProgress(workDone, max)
                        override fun updateTitle(title: String?) = taskObj.updateTitle(title)
                    })
                }
            }
        }
}