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

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.export.api.BaseExporter
import com.dansoftware.boomega.export.api.ExportProcessObserver
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.export.ConfigurationDialog
import com.dansoftware.boomega.gui.util.icon
import javafx.scene.Node
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.OutputStream
import java.io.OutputStreamWriter

class YamlExporter : BaseExporter<YamlExportConfiguration>() {
    override val name: String
        get() = "Yaml" // TODO: i18n

    override val icon: Node
        get() = icon("yaml-icon")

    override val configurationDialog: ConfigurationDialog<YamlExportConfiguration>
        get() = object : ConfigurationDialog<YamlExportConfiguration> {
            override fun show(context: Context, onFinished: (YamlExportConfiguration) -> Unit) {
                onFinished(YamlExportConfiguration())
            }
        }

    override val contentType: String
        get() = "yaml"

    override val contentTypeDescription: String
        get() = "yaml files" // todo: i18n

    override fun write(
        items: List<Record>,
        output: OutputStream,
        config: YamlExportConfiguration,
        observer: ExportProcessObserver
    ) {
        OutputStreamWriter(output).buffered().use {
            buildYamlProcessor().dumpAll(sortRecords(items, config).iterator(), it)
        }
    }

    private fun buildYamlProcessor() = Yaml(Constructor(Record::class.java)).apply {
    }
}