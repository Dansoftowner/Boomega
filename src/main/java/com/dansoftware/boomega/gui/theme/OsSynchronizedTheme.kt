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

import com.dansoftware.boomega.i18n.i18n
import com.jthemedetecor.OsThemeDetector
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedList
import java.util.function.Consumer

object OsSynchronizedTheme : Theme() {

    private val logger: Logger = LoggerFactory.getLogger(OsSynchronizedTheme::class.java)

    override val name: String
        get() = i18n("app.ui.theme.sync")

    private val osThemeDetector
        get() = OsThemeDetector.getDetector()

    private val themeChangeEventHandlers =
        synchronizedList(mutableListOf<(isDark: Boolean) -> Boolean>())

    private val themeDetectionListener = Consumer<Boolean> { isDark ->
        Platform.runLater {
            val iterator = themeChangeEventHandlers.iterator()
            while (iterator.hasNext()) {
                val eventHandler = iterator.next()
                val eventHandlerShouldStay = eventHandler(isDark)
                if (!eventHandlerShouldStay) {
                    iterator.remove()
                }
            }
        }
    }

    override fun init() {
        super.init()
        osThemeDetector.registerListener(themeDetectionListener)
    }

    override fun destroy() {
        super.destroy()
        logger.debug("OsSynchronizedTheme dropped!")
        logger.debug("Removing os theme listener...")
        osThemeDetector.removeListener(themeDetectionListener)
    }

    override fun apply(scene: Scene) {
        registerScene(WeakReference(scene))
    }

    override fun apply(region: Parent) {
        registerRegion(WeakReference(region))
    }

    override fun deApply(scene: Scene) {
        getAppropriateTheme(osThemeDetector.isDark).deApply(scene)
    }

    override fun deApply(region: Parent) {
        getAppropriateTheme(osThemeDetector.isDark).deApply(region)
    }

    private fun registerScene(scene: WeakReference<Scene>) {
        fun apply(isDark: Boolean): Boolean =
            scene.get()?.let {
                getAppropriateTheme(!isDark).deApply(it)
                getAppropriateTheme(isDark).apply(it)
            } != null
        apply(osThemeDetector.isDark)
        themeChangeEventHandlers.add(::apply)
    }

    private fun registerRegion(region: WeakReference<Parent>) {
        fun apply(isDark: Boolean): Boolean =
            region.get()?.let {
                getAppropriateTheme(!isDark).deApply(it)
                getAppropriateTheme(isDark).apply(it)
            } != null
        apply(osThemeDetector.isDark)
        themeChangeEventHandlers.add(::apply)
    }

    private fun getAppropriateTheme(isDark: Boolean) =
        if (isDark) DarkTheme else LightTheme
}