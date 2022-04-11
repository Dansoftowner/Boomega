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

package com.dansoftware.boomega.gui.firsttime.segment.lang

import com.dansoftware.boomega.config.LOCALE
import com.dansoftware.boomega.i18n.api.I18N
import javafx.geometry.Insets
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import java.util.*

class LanguageSegmentView(private val preferences: com.dansoftware.boomega.config.Preferences) : StackPane() {

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.add(buildListView())
    }

    private fun buildListView() = ListView<LanguageEntry>().apply {
        selectionModel.selectedItemProperty().addListener { _, oldItem, newItem ->
            newItem?.let {
                preferences.editor().put(LOCALE, it.locale)
                Locale.setDefault(it.locale)
            } ?: selectionModel.select(oldItem) //we don't allow the user to choose no items
        }
        fillListView()
    }

    private fun ListView<LanguageEntry>.fillListView() {
        val availableLocales = ArrayList(I18N.getAvailableLocales())
        val defaultLocaleIndex = availableLocales.indexOf(I18N.defaultLocale())
        items.addAll(availableLocales.map(::LanguageEntry))
        selectionModel.select(defaultLocaleIndex)
        scrollTo(defaultLocaleIndex)
    }


    /**
     * Represents an item in the ListView
     */
    private class LanguageEntry(val locale: Locale) {
        override fun toString(): String {
            return locale.displayName
        }
    }
}