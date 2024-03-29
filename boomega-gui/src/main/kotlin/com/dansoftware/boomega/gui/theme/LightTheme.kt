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

package com.dansoftware.boomega.gui.theme

import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.res
import javafx.scene.Parent
import javafx.scene.Scene
import jfxtras.styles.jmetro.Style
import javax.inject.Singleton

/**
 * Applies light appearance to the UI components.
 */
@Singleton
open class LightTheme : JMetroTheme(Style.LIGHT) {

    override val name: String
        get() = i18n("app.ui.theme.light")

    override fun apply(region: Parent) {
        super.apply(region)
        region.stylesheets.add(STYLE_SHEET)
    }

    override fun apply(scene: Scene) {
        super.apply(scene)
        scene.stylesheets.add(STYLE_SHEET)
    }

    override fun deApply(region: Parent) {
        super.deApply(region)
        region.stylesheets.remove(STYLE_SHEET)
    }

    override fun deApply(scene: Scene) {
        super.deApply(scene)
        scene.stylesheets.remove(STYLE_SHEET)
    }

    companion object {
        private val STYLE_SHEET = res("light.css", LightTheme::class)!!.toExternalForm()
    }
}