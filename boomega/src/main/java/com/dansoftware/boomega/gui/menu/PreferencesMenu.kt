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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.action.OpenSettingsAction
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.theme.THEME
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.LOCALE
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.main.ApplicationRestart
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.ToggleGroup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class PreferencesMenu(private val context: Context) : Menu(i18n("menubar.menu.preferences")) {

    init {
        this.menuItem(menuItemOf(OpenSettingsAction, context))
            .separator()
            .menuItem(themeMenu())
            .menuItem(langMenu())
    }

    private fun themeMenu() = object : Menu(i18n("menubar.menu.preferences.theme")) {

        private val themeChangeListener = Theme.DefaultThemeListener { _, newTheme ->
            items.forEach { if (it is RadioMenuItem) it.isSelected = newTheme.javaClass == it.userData }
        }

        init {
            Theme.registerListener(themeChangeListener)
            this.graphic("paint-icon")
            this.buildItems()
        }

        private fun buildItems() {
            val radioGroup = ToggleGroup()
            Theme.available.forEach { theme ->
                this.menuItem(RadioMenuItem(theme.name).apply {
                    toggleGroup = radioGroup
                    isSelected = Theme.default.javaClass == theme.javaClass
                    setOnAction {
                        try {
                            Theme.default = theme
                            logger.debug("Default theme set: '{}'", theme.javaClass.name)
                            get(com.dansoftware.boomega.config.Preferences::class).editor().put(THEME, theme)
                            // we explicitly set it selected for avoiding some buggy behaviour
                            isSelected = true
                        } catch (e: Exception) {
                            logger.error("Couldn't set the theme", e)
                            // TODO: error dialog
                        }
                    }
                })
            }
        }
    }

    private fun langMenu() = Menu(i18n("menubar.menu.preferences.lang"))
        .also { menu ->
            val toggleGroup = ToggleGroup()
            I18N.getAvailableLocales().forEach { locale ->
                menu.menuItem(RadioMenuItem(locale.displayLanguage).also {
                    it.toggleGroup = toggleGroup
                    it.isSelected = Locale.getDefault() == locale
                    it.setOnAction {
                        get(com.dansoftware.boomega.config.Preferences::class).editor().put(LOCALE, locale)
                        context.showConfirmationDialog(
                            i18n("app.lang.restart.title"),
                            i18n("app.lang.restart.msg")
                        ) { btn ->
                            when {
                                btn.typeEquals(ButtonType.YES) -> ApplicationRestart.restart()
                            }
                        }
                    }
                })
            }
        }
        .graphic("translate-icon")

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PreferencesMenu::class.java)
    }
}