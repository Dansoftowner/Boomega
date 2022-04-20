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

package com.dansoftware.boomega.gui.databaseview

import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.onScenePresent
import com.dansoftware.boomega.i18n.api.i18n
import javafx.css.PseudoClass
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The [ModuleView] (also called as 'Home view') is a panel that allows the user to open the basic modules.
 *
 * @see Module
 */
class ModuleView(private val view: DatabaseView) : StackPane() {

    init {
        styleClass.add("module-view")
        buildUI()
        playAnimation()
    }

    private fun playAnimation() {
        onScenePresent {
            animatefx.animation.FadeInUp(this).play()
        }
    }

    private fun buildUI() {
        children.add(buildCenterBox())
    }

    private fun buildCenterBox() = Group(
        VBox(20.0,
            buildLabelArea(),
            buildPagination()
        )
    )

    private fun buildLabelArea() =
        StackPane(
            Group(
                HBox(10.0).apply {
                    styleClass.add("label-area")
                    children.add(StackPane(ImageView()))
                    children.add(StackPane(buildAppLabel()))
                }
            )
        )

    private fun buildAppLabel() =
        Label(System.getProperty("app.name"))

    private fun buildPagination() = Pagination().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add(Pagination.STYLE_CLASS_BULLET)
        styleClass.add("tile-pagination")
        pageCountProperty().addListener { _, _, count ->
            // adding the "one-page" pseudo-class if the pagination has only one page
            pseudoClassStateChanged(PseudoClass.getPseudoClass("one-page"), count == 1)
        }
        val gridPanes = buildGridPanes()
        pageCount = gridPanes.size
        setPageFactory { gridPanes[it] }
    }

    private fun buildGridPanes(): List<GridPane> =
        buildTiles()
            .chunked(TILES_PER_ROW)
            .chunked(ROWS_PER_PAGE)
            .map { rowsPerGrid ->
                GridPane().apply {
                    styleClass.add("tile-grid")
                    rowsPerGrid.forEach {
                        addRow(rowCount, *it.toTypedArray())
                    }
                }
            }

    private fun buildTiles(): List<Node> = view.modules.map(::buildTile)

    private fun buildTile(module: Module) = Tile(view, module)

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(ModuleView::class.java)

        private const val TILES_PER_ROW = 3
        private const val ROWS_PER_PAGE = 3

        /**
         * Gives a [TabItem] that can show a [ModuleView]
         */
        fun getTabItem(context: DatabaseView) =
            TabItem(
                "moduleview",
                i18n("database_view.home"),
                { icon("home-icon") }) {
                ModuleView(context)
            }
    }

    private class Tile(val view: DatabaseView, val module: Module) : VBox() {
        init {
            styleClass.add("tile")
            id = "tile-${module.id}"
            spacing = 10.0
            buildUI()
        }

        private fun buildUI() {
            children.add(StackPane(buildButton()))
            children.add(StackPane(buildLabel()))
        }

        private fun buildLabel() = Label(module.name).apply {
            tooltip = Tooltip(module.name)
        }

        private fun buildButton() = Button().apply {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = module.icon
            tooltip = Tooltip(module.name)
            setOnAction { view.openTab(module.getTabItem()) }
        }
    }
}