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

package com.dansoftware.boomega.gui.databaseview

import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

/**
 * The [ModuleView] is a panel that allows the user to open the basic modules.
 *
 * @see Module
 */
class ModuleView(
    private val view: DatabaseView
) : StackPane() {

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(buildCenterBox())
    }

    private fun buildCenterBox() = Group(
        VBox(
            10.0,
            buildGridPane()
        )
    )

    private fun buildGridPane() = GridPane().apply {
        hgap = 10.0
        vgap = 10.0
        buildTiles()
            .chunked(TILES_PER_ROW)
            .forEach { addRow(rowCount, *it.toTypedArray()) }
    }

    private fun buildTiles(): List<Node> = view.modules.map(this::buildTile)

    private fun buildTile(module: Module) =
        Button(module.name, module.icon).apply {
            contentDisplay = ContentDisplay.TOP
            styleClass.add("tile")
            id = "tile-${module.id}"
            setOnAction { view.openTab(module.asTabItem()) }
        }

    companion object {

        private const val TILES_PER_ROW = 3

        /**
         * Gives a [TabItem] that can show a [ModuleView]
         */
        fun asTabItem(context: DatabaseView) =
            TabItem("moduleview", I18N.getValue("database_view.tab.modules"), { MaterialDesignIconView(MaterialDesignIcon.HOME) }) {
                ModuleView(context)
            }
    }
}