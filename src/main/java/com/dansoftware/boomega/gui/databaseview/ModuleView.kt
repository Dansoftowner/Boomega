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
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Pagination
import javafx.scene.layout.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The [ModuleView] is a panel that allows the user to open the basic modules.
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
        // we play the animation after the view put into a scene
        sceneProperty().addListener(object : ChangeListener<Scene?> {
            override fun changed(observable: ObservableValue<out Scene?>, oldValue: Scene?, newValue: Scene?) {
                newValue?.let {
                    logger.debug("Scene detected!")
                    animatefx.animation.FadeInUp(this@ModuleView).play()
                    observable.removeListener(this)
                }
            }
        })
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
                    children.add(StackPane(MaterialDesignIconView(MaterialDesignIcon.VIEW_MODULE)))
                    children.add(StackPane(Label(I18N.getValue("database_view.modules"))))
                }
            )
        )

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

    private fun buildTiles(): List<Node> =
        view.modules.map(this::buildTile)

    private fun buildTile(module: Module) =
        Button(module.name, module.icon).apply {
            contentDisplay = ContentDisplay.TOP
            styleClass.add("tile")
            id = "tile-${module.id}"
            setOnAction { view.openTab(module.asTabItem()) }
        }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(ModuleView::class.java)

        private const val TILES_PER_ROW = 3
        private const val ROWS_PER_PAGE = 3

        /**
         * Gives a [TabItem] that can show a [ModuleView]
         */
        fun asTabItem(context: DatabaseView) =
            TabItem(
                "moduleview",
                I18N.getValue("database_view.modules"),
                { MaterialDesignIconView(MaterialDesignIcon.VIEW_MODULE) }) {
                ModuleView(context)
            }
    }
}