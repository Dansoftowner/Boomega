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

package com.dansoftware.boomega.export.txtable

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.export.api.BaseExporter
import com.dansoftware.boomega.export.api.ConfigurationDialog
import com.dansoftware.boomega.export.api.ExportProcessObserver
import com.dansoftware.boomega.export.txtable.gui.TXTableConfigurationDialog
import com.dansoftware.boomega.gui.util.icon
import com.inamik.text.tables.GridTable
import com.inamik.text.tables.SimpleTable
import com.inamik.text.tables.grid.Border
import com.inamik.text.tables.grid.Util
import javafx.scene.Node
import java.io.OutputStream
import java.io.PrintStream

/**
 * Allows exporting [Record]s into txt tables.
 */
class TXTableExporter : BaseExporter<TXTableConfiguration>() {

    override val name: String
        get() = "TXT Table"

    override val icon: Node
        get() = icon("txt-icon")

    override val configurationDialog: ConfigurationDialog<TXTableConfiguration>
        get() = TXTableConfigurationDialog()

    override val contentType: String
        get() = "txt"

    override val contentTypeDescription: String
        get() = "Txt table"

    override fun write(
        items: List<Record>,
        output: OutputStream,
        config: TXTableConfiguration,
        observer: ExportProcessObserver
    ) {
        PrintStream(output.buffered(), true, charset("UTF-8")).use {
            val grid = buildGrid(sortRecords(items, config), config)
            Util.print(grid, it)
        }
    }

    private fun buildGrid(items: List<Record>, config: TXTableConfiguration): GridTable {

        val table = SimpleTable.of().run {
            nextRow()
            config.requiredFields.forEach {
                nextCell()
                addLine(it.name)
                applyHeaderConfigToCell(config)
            }

            items.forEach { record ->
                nextRow()
                config.requiredFields.forEach { field ->
                    nextCell()
                    addLine(field.getValue(record)?.toString() ?: config.nullValuePlaceHolder)
                    applyRegularConfigToCell(config)
                }
            }

            toGrid()
        }

        return config.border.run {
            Border.of(Border.Chars.of(intersect, horizontal, vertical)).apply(table)
        }
    }

    private fun SimpleTable.applyHeaderConfigToCell(config: TXTableConfiguration) {
        applyToCell(config.headerVerticalAlignment.internalType.withHeight(config.headerHeight))
        applyToCell(config.headerHorizontalAlignment.internalType.withWidth(config.headerMinWidth).withChar(config.headerPlaceHolderChar))
    }

    private fun SimpleTable.applyRegularConfigToCell(config: TXTableConfiguration) {
        applyToCell(config.verticalAlignment.internalType.withHeight(config.regularHeight))
        applyToCell(config.horizontalAlignment.internalType.withWidth(config.regularMinWidth).withChar(config.regularPlaceHolderChar))
    }

    private val TXTableConfiguration.HorizontalAlignment.internalType
        get() = when (this) {
            TXTableConfiguration.HorizontalAlignment.LEFT -> com.inamik.text.tables.Cell.Functions.LEFT_ALIGN
            TXTableConfiguration.HorizontalAlignment.RIGHT -> com.inamik.text.tables.Cell.Functions.RIGHT_ALIGN
            TXTableConfiguration.HorizontalAlignment.CENTER -> com.inamik.text.tables.Cell.Functions.HORIZONTAL_CENTER
        }

    private val TXTableConfiguration.VerticalAlignment.internalType
        get() = when (this) {
            TXTableConfiguration.VerticalAlignment.TOP -> com.inamik.text.tables.Cell.Functions.TOP_ALIGN
            TXTableConfiguration.VerticalAlignment.CENTER -> com.inamik.text.tables.Cell.Functions.VERTICAL_CENTER
            TXTableConfiguration.VerticalAlignment.BOTTOM -> com.inamik.text.tables.Cell.Functions.BOTTOM_ALIGN
        }
}