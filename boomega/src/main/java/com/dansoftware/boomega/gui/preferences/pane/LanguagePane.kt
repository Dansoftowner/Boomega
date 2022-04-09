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

import com.dansoftware.boomega.config.LOCALE
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.main.ApplicationRestart
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceBox
import javafx.util.StringConverter
import java.util.*

class LanguagePane(
    private val context: Context,
    preferences: com.dansoftware.boomega.config.Preferences
) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.language")
    override val graphic: Node = icon("translate-icon")

    override fun buildContent(): Content = object : Content() {

        init {
            buildItems()
        }

        private fun buildItems() {
            items.add(buildLanguageSelectControl())
        }

        private fun buildLanguageSelectControl(): PreferencesControl =
            ChoiceBox<Locale>().run {
                this.converter = object : StringConverter<Locale>() {
                    override fun toString(locale: Locale?): String = locale?.displayLanguage ?: ""
                    override fun fromString(string: String?): Locale = Locale.forLanguageTag(string)
                }

                I18N.getAvailableLocales().forEach {
                    this.items.add(it)
                    if (it == preferences.get(LOCALE))
                        this.selectionModel.select(it)
                }

                this.selectionModel.selectedItemProperty().addListener { _, _, locale ->
                    preferences.editor().put(LOCALE, locale)
                    context.showConfirmationDialog(
                        I18N.getValue("app.lang.restart.title"),
                        I18N.getValue("app.lang.restart.msg")
                    ) { btn ->
                        btn.takeIf { it.typeEquals(ButtonType.YES) }?.let {
                            ApplicationRestart.restart()
                        }
                    }
                }

                PairControl(
                    I18N.getValue("preferences.language.lang"),
                    I18N.getValue("preferences.language.lang.desc"),
                    this
                )
            }

    }
}