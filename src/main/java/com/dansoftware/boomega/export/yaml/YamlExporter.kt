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
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.*

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
            buildYamlProcessor(config).dump(sortRecords(items, config), it)
        }
    }

    private fun buildYamlProcessor(config: YamlExportConfiguration) =
        Yaml(Constructor(Record::class.java), RepresenterImpl(), buildDumperOptions(config))

    private fun buildDumperOptions(config: YamlExportConfiguration) = DumperOptions().apply {
        defaultScalarStyle = config.defaultScalarStyle.snakeYamlType
        defaultFlowStyle = config.defaultFlowStyle.snakeYamlType
        isCanonical = config.isCanonical
        isAllowUnicode = config.isAllowUnicode
        indent = config.indent
        width = config.bestWidth
        splitLines = config.splitLines
        isExplicitStart = config.explicitStart
        isExplicitEnd = config.explicitEnd
        isPrettyFlow = config.prettyFlow
    }


    private val YamlExportConfiguration.ScalarStyle.snakeYamlType get() =
        when(this) {
            YamlExportConfiguration.ScalarStyle.DOUBLE_QUOTED -> DumperOptions.ScalarStyle.DOUBLE_QUOTED
            YamlExportConfiguration.ScalarStyle.SINGLE_QUOTED -> DumperOptions.ScalarStyle.SINGLE_QUOTED
            YamlExportConfiguration.ScalarStyle.LITERAL -> DumperOptions.ScalarStyle.LITERAL
            YamlExportConfiguration.ScalarStyle.FOLDED -> DumperOptions.ScalarStyle.FOLDED
            YamlExportConfiguration.ScalarStyle.PLAIN -> DumperOptions.ScalarStyle.PLAIN
        }

    private val YamlExportConfiguration.FlowStyle.snakeYamlType get() =
        when(this) {
            YamlExportConfiguration.FlowStyle.FLOW -> DumperOptions.FlowStyle.FLOW
            YamlExportConfiguration.FlowStyle.BLOCK -> DumperOptions.FlowStyle.BLOCK
            YamlExportConfiguration.FlowStyle.AUTO -> DumperOptions.FlowStyle.AUTO
        }

    private class RepresenterImpl : Representer() {
        init {
            // for excluding type definitions
            classTags[Record::class.java] = Tag.MAP
            representers[Locale::class.java] = LocaleRepresent()
        }
    }

    private class LocaleRepresent : Represent {
        override fun representData(data: Any?): org.yaml.snakeyaml.nodes.Node? {
            return (data as? Locale)?.let {
                representScalar(it.toLanguageTag())
            }
        }

        private fun representScalar(value: String) =
            ScalarNode(Tag.STR, value, null, null, DumperOptions.ScalarStyle.PLAIN)
    }
}