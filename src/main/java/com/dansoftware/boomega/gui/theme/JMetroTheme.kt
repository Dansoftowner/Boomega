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

package com.dansoftware.boomega.gui.theme

import com.dansoftware.boomega.util.ReflectionUtils
import javafx.scene.Parent
import javafx.scene.Scene
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import kotlin.reflect.KClass

/**
 * A [Theme] that applies [JMetro] styles on the given UI components.
 */
abstract class JMetroTheme(val style: Style) : Theme() {

    /**
     * Stylesheets that should be applied additionally outside the JMetro styles.
     */
    open val additionalStyleSheets: List<String>
        get() = emptyList()


    override fun apply(scene: Scene) {
        JMetro(style).scene = scene
        scene.stylesheets.addAll(additionalStyleSheets)
    }

    override fun apply(region: Parent) {
        JMetro(style).parent = region
        region.stylesheets.addAll(additionalStyleSheets)
    }

    override fun deApply(scene: Scene) {
        scene.stylesheets.removeAll(jMetroStyleSheets)
        scene.stylesheets.removeAll(additionalStyleSheets)
    }

    override fun deApply(region: Parent) {
        region.stylesheets.removeAll(jMetroStyleSheets)
        region.stylesheets.removeAll(additionalStyleSheets)
    }

    private companion object {
        val jMetroStyleSheets: List<String> by lazy {
            getDeclaredFields(
                JMetro::class,
                "BASE_STYLESHEET_URL",
                "BASE_EXTRAS_STYLESHEET_URL",
                "BASE_OTHER_LIBRARIES_STYLESHEET_URL"

            ).plus(
                getDeclaredFields(
                    Style::class,
                    "DARK_STYLE_SHEET_URL",
                    "LIGHT_STYLE_SHEET_URL"
                )
            ).toList()
        }

        private fun getDeclaredFields(classRef: KClass<*>, vararg fieldNames: String) =
            fieldNames.asSequence()
                .map(classRef.java::getDeclaredField)
                .onEach { it.isAccessible = true }
                .map(ReflectionUtils::getDeclaredStaticValue)
                .map(Any::toString)
    }
}