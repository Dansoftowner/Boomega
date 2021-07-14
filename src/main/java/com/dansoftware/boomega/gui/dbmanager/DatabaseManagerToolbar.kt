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

package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import org.apache.commons.lang3.StringUtils

class DatabaseManagerToolbar(private val view: DatabaseManagerView) : BiToolBar() {

    init {
        leftToolBar.padding = Insets(0.0, 0.0, 0.0, 10.0)
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildIcon())
        leftItems.add(buildLabel())
        rightItems.add(buildSelectedItemsIndicator())
        rightItems.add(buildRefreshButton())
    }

    private fun buildIcon() = MaterialDesignIconView(MaterialDesignIcon.DATABASE)

    private fun buildLabel() = Label(I18N.getValue("database.manager.title"))

    private fun buildSelectedItemsIndicator() = Label().apply {
        textProperty().bind(
            view.tableItemsCount().asString()
                .concat("/")
                .concat(view.selectedTableItemsCount())
                .concat(StringUtils.SPACE)
                .concat(I18N.getValue("database.manager.selected"))
        )
    }

    private fun buildRefreshButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.RELOAD)
        setOnAction {
            view.refresh()
            animatefx.animation.RotateIn(graphic).play();
        }
    }
}