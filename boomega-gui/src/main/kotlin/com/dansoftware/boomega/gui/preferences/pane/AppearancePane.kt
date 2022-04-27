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

import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.config.THEME
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.api.I18N
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.util.StringConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppearancePane(preferences: com.dansoftware.boomega.config.Preferences) : PreferencesPane(preferences) {
    override val title = I18N.getValue("preferences.tab.appearance")
    override val graphic: Node = icon("paint-icon")

    override fun buildContent(): Content = object : Content() {
        init {
            buildItems()
        }

        private fun buildItems() {
            items.add(buildThemeSelect())
            items.add(buildWindowOpacitySlider())
        }

        private fun buildWindowOpacitySlider(): PreferencesControl {
            return Slider(20.0, 100.0, BaseWindow.globalOpacity.value * 100).run {
                valueProperty().addListener { _, _, value ->
                    value.toDouble().div(100)
                        .let(BaseWindow.globalOpacity::set)
                }
                valueChangingProperty().addListener { _, _, changing ->
                    changing.takeIf { it.not() }?.let {
                        logger.debug("Global opacity saved to configurations")
                        preferences.editor().put(BaseWindow.GLOBAL_OPACITY_CONFIG_KEY, value.div(100))
                    }
                }

                PairControl(
                    I18N.getValue("preferences.appearance.window_opacity"),
                    I18N.getValue("preferences.appearance.window_opacity.desc"),
                    this
                )
            }
        }

        private fun buildThemeSelect(): PreferencesControl {
            return ChoiceBox<Theme>().run {

                this.converter = object : StringConverter<Theme?>() {
                    override fun toString(themeMeta: Theme?): String {
                        return themeMeta?.name ?: ""
                    }

                    override fun fromString(string: String?): Theme? {
                        //TODO("Not yet implemented")
                        return null
                    }
                }

                Theme.available.forEach {
                    if (Theme.default.javaClass == it.javaClass)
                        selectionModel.select(it)
                    items.add(it)
                }

                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    try {
                        Theme.default = it
                        logger.debug("Default theme set: '{}'", it.javaClass.name)
                        preferences.editor().put(THEME, it)
                    } catch (e: Exception) {
                        logger.error("Couldn't set the theme", e)
                        // TODO: error dialog
                    }
                }

                PairControl(
                    I18N.getValue("preferences.appearance.theme"),
                    I18N.getValue("preferences.appearance.theme.desc"),
                    this
                )
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppearancePane::class.java)
    }
}