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

package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.ThemeMeta
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.ReflectionUtils
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.util.StringConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppearancePane(preferences: Preferences) : PreferencesPane(preferences) {
    override val title = I18N.getValue("preferences.tab.appearance")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.FORMAT_PAINT)

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
            return ChoiceBox<ThemeMeta<*>>().run {

                this.converter = object : StringConverter<ThemeMeta<*>?>() {
                    override fun toString(themeMeta: ThemeMeta<*>?): String {
                        return themeMeta?.displayNameSupplier?.get() ?: ""
                    }

                    override fun fromString(string: String?): ThemeMeta<*>? {
                        //TODO("Not yet implemented")
                        return null
                    }
                }

                Theme.getAvailableThemesData().forEach {
                    if (Theme.getDefault().javaClass == it.themeClass)
                        selectionModel.select(it)
                    items.add(it)
                }

                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    try {
                        val themeObject = ReflectionUtils.constructObject(it.themeClass)
                        logger.debug("The theme object: {}", themeObject)
                        Theme.setDefault(themeObject)
                        preferences.editor().put(PreferenceKey.THEME, themeObject)
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