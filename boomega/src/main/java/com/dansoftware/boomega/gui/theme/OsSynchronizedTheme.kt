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
import com.jthemedetecor.OsThemeDetector
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedList
import java.util.function.Consumer

/**
 * Synchronizes the appearance of the UI components based on what's the system's preferred theme (dark/ light).
 */
open class OsSynchronizedTheme : Theme() {

    override val name: String
        get() = i18n("app.ui.theme.sync")

    /**
     * Specifies the [Theme] should be used when the system is in dark mode
     */
    open val darkTheme: Theme
        get() = DarkTheme.INSTANCE

    /**
     * Specifies the [Theme] should be used when the system is in light mode
     */
    open val lightTheme: Theme
        get() = LightTheme.INSTANCE

    /**
     * The list of functions that should be invoked by the os theme detection listener, when
     * the theme changes.
     */
    private val themeChangeEventHandlers =
        synchronizedList(mutableListOf<(isDark: Boolean) -> Boolean>())

    /**
     * The theme-detection listener
     */
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

    private inline val osThemeDetector
        get() = OsThemeDetector.getDetector()

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
        val applyFunction = fun(isDark: Boolean): Boolean =
            scene.get()?.let {
                getAppropriateTheme(!isDark).deApply(it)
                getAppropriateTheme(isDark).apply(it)
            } != null
        applyFunction(osThemeDetector.isDark)
        themeChangeEventHandlers.add(applyFunction)
    }

    private fun registerRegion(region: WeakReference<Parent>) {
        val applyFunction = fun(isDark: Boolean): Boolean =
            region.get()?.let {
                getAppropriateTheme(!isDark).deApply(it)
                getAppropriateTheme(isDark).apply(it)
            } != null
        applyFunction(osThemeDetector.isDark)
        themeChangeEventHandlers.add(applyFunction)
    }

    private fun getAppropriateTheme(isDark: Boolean) =
        if (isDark) darkTheme else lightTheme

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(OsSynchronizedTheme::class.java)

        /**
         * A global instance of the [OsSynchronizedTheme]
         */
        @JvmField
        val INSTANCE = OsSynchronizedTheme()
    }
}