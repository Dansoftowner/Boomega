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

import com.dansoftware.boomega.util.ReflectionUtils
import javafx.scene.Parent
import javafx.scene.Scene
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.reflect.KClass

/**
 * A [Theme] that applies [JMetro] styles on the given UI components.
 */
abstract class JMetroTheme(val style: Style) : Theme() {

    @MustBeInvokedByOverriders
    override fun apply(scene: Scene) {
        JMetro(style).scene = scene
    }

    @MustBeInvokedByOverriders
    override fun apply(region: Parent) {
        JMetro(style).parent = region
    }

    @MustBeInvokedByOverriders
    override fun deApply(scene: Scene) {
        scene.stylesheets.removeAll(jMetroStyleSheets)
    }

    @MustBeInvokedByOverriders
    override fun deApply(region: Parent) {
        region.stylesheets.removeAll(jMetroStyleSheets)
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