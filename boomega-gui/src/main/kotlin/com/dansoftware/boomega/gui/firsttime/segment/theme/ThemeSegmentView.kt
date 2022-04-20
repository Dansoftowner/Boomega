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

package com.dansoftware.boomega.gui.firsttime.segment.theme

import com.dansoftware.boomega.gui.theme.*
import com.dansoftware.boomega.i18n.api.I18N
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThemeSegmentView(private val preferences: com.dansoftware.boomega.config.Preferences) : StackPane() {

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(Group(buildHBox()))
    }

    private fun buildHBox() = HBox(10.0).apply {
        buildRadioGroup().also { group ->
            children.add(
                buildToggle(
                    "segment.theme.light",
                    "/com/dansoftware/boomega/image/firsttime/ThemeLight.png",
                    group,
                    LightTheme.INSTANCE
                )
            )

            children.add(
                buildToggle(
                    "segment.theme.dark",
                    "/com/dansoftware/boomega/image/firsttime/ThemeDark.png",
                    group,
                    DarkTheme.INSTANCE
                )
            )

            children.add(
                buildToggle(
                    "segment.theme.sync",
                    "/com/dansoftware/boomega/image/firsttime/ThemeSynchronized.png",
                    group,
                    OsSynchronizedTheme.INSTANCE
                )
            )
        }
    }

    private fun <T : Theme> buildToggle(
        i18n: String,
        thumbnailPath: String,
        group: ToggleGroup,
        theme: T
    ) = ThemeToggle(
        I18N.getValue(i18n),
        ImageView(thumbnailPath).apply {
            fitHeight = 245.0
            fitWidth = 233.0
        },
        theme
    ).apply { toggleGroup = group }

    private fun buildRadioGroup() = ToggleGroup().apply {
        selectedToggleProperty().addListener { _, _, newItem ->
            newItem?.let {
                if (it is ThemeToggle<*>) {
                    preferences.editor().put(THEME, it.theme).tryCommit()
                    Theme.default = it.theme
                    logger.debug("Theme selected: {}", Theme.default.javaClass.name)
                }
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ThemeSegmentView::class.java)
    }

    private class ThemeToggle<T : Theme>(
        text: String,
        thumbnail: Node,
        val theme: T
    ) : RadioButton() {
        init {
            this.text = text
            this.contentDisplay = ContentDisplay.BOTTOM
            this.graphic = thumbnail
            this.isSelected = Theme.default.javaClass == theme.javaClass
            this.padding = Insets(10.0)
        }
    }
}