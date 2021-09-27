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

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.export.api.BaseExporter
import com.dansoftware.boomega.export.api.ExportProcessObserver
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.export.ConfigurationDialog
import com.dansoftware.boomega.gui.util.icon
import com.inamik.text.tables.GridTable
import com.inamik.text.tables.SimpleTable
import com.inamik.text.tables.grid.Util
import javafx.scene.Node
import java.io.OutputStream
import java.io.PrintStream


class TXTableExporter : BaseExporter<TXTableConfiguration>() {

    override val name: String
        get() = "TXT Table"

    override val icon: Node
        get() = icon("txt-icon")

    override val configurationDialog: ConfigurationDialog<TXTableConfiguration>
        get() = object : ConfigurationDialog<TXTableConfiguration> {
            override fun show(context: Context, onFinished: (TXTableConfiguration) -> Unit) {
                onFinished(TXTableConfiguration())
            }

        }

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
        PrintStream(output.buffered()).use {
            val grid = buildGrid(sortRecords(items, config), config)
            Util.print(grid, it)
        }
    }

    private fun buildGrid(items: List<Record>, config: TXTableConfiguration): GridTable {
        val table = SimpleTable.of()
        table.nextRow().apply {
            config.requiredFields.forEach {
                nextCell()
                addLine(it.name)
            }
        }

        return table.toGrid();

        /* val height = 10
        val width = 10

        val s: SimpleTable = SimpleTable.of()
            .nextRow()
            .nextCell()
            .addLine("Left")
            .addLine("Top")
            .applyToCell(TOP_ALIGN.withHeight(height))
            .applyToCell(LEFT_ALIGN.withWidth(width).withChar('^'))
            .nextCell()
            .addLine("Center")
            .addLine("Top")
            .applyToCell(TOP_ALIGN.withHeight(height))
            .applyToCell(HORIZONTAL_CENTER.withWidth(width))
            .nextCell()
            .addLine("Right")
            .addLine("Top")
            .applyToCell(TOP_ALIGN.withHeight(height))
            .applyToCell(RIGHT_ALIGN.withWidth(width))
            .nextRow()
            .nextCell()
            .addLine("Left")
            .addLine("Center")
            .applyToCell(VERTICAL_CENTER.withHeight(height))
            .applyToCell(LEFT_ALIGN.withWidth(width))
            .nextCell()
            .addLine("Center")
            .addLine("Center")
            .applyToCell(VERTICAL_CENTER.withHeight(height))
            .applyToCell(HORIZONTAL_CENTER.withWidth(width).withChar('.'))
            .nextCell()
            .addLine("Right")
            .addLine("Center")
            .applyToCell(VERTICAL_CENTER.withHeight(height))
            .applyToCell(RIGHT_ALIGN.withWidth(width))
            .nextRow()
            .nextCell()
            .addLine("Left")
            .addLine("Bottom")
            .applyToCell(BOTTOM_ALIGN.withHeight(height))
            .applyToCell(LEFT_ALIGN.withWidth(width))
            .nextCell()
            .addLine("Center")
            .addLine("Bottom")
            .applyToCell(BOTTOM_ALIGN.withHeight(height))
            .applyToCell(HORIZONTAL_CENTER.withWidth(width))
            .nextCell()
            .addLine("Right")
            .addLine("Bottom")
            .applyToCell(BOTTOM_ALIGN.withHeight(height))
            .applyToCell(RIGHT_ALIGN.withWidth(width).withChar('_'))

        var g = s.toGrid()

        g = Border.of(Border.Chars.of('+', '-', '|')).apply(g)

        Util.print(g, PrintStream(output))*/
    }
}