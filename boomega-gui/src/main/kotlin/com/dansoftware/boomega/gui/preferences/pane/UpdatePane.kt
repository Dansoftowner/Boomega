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

package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.gui.util.asCentered
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.update.LAST_UPDATE_SEARCH
import com.dansoftware.boomega.update.SEARCH_UPDATES
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import java.time.format.DateTimeFormatter

class UpdatePane(preferences: com.dansoftware.boomega.config.Preferences) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.update")
    override val graphic: Node = icon("update-icon")

    override fun buildContent(): Content = object : Content() {
        init {
            buildItems()
        }

        private fun buildItems() {
            items.add(buildAutoSearchToggle())
            items.add(buildLastTimeSearchLabel())
        }

        private fun buildAutoSearchToggle(): PreferencesControl =
            ToggleControl(
                i18n("preferences.update.automatic"),
                i18n("preferences.update.automatic.desc")
            ).apply {
                isSelected = preferences.get(SEARCH_UPDATES)
                selectedProperty().addListener { _, _, selected ->
                    preferences.editor().put(SEARCH_UPDATES, selected)
                }
            }

        private fun buildLastTimeSearchLabel() = PairControl(
            title = i18n("preferences.update.last"),
            customControl = Label(
                preferences.get(LAST_UPDATE_SEARCH)
                    ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ).asCentered(Pos.CENTER_RIGHT)
        )

    }
}