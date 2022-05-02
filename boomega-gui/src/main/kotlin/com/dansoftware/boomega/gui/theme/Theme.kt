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

import com.dansoftware.boomega.di.DIService.get
import javafx.scene.Parent
import javafx.scene.Scene
import org.jetbrains.annotations.MustBeInvokedByOverriders
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedList
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A Theme can change the appearance of UI elements.
 */
abstract class Theme {

    /**
     * The theme's name. Generally an internationalized string.
     */
    abstract val name: String

    /**
     * Returns:
     * - _true_ - if the [init] method has been called,
     * - _false_ - if the [init] method has not been invoked or the [destroy] method has been invoked after it
     */
    var isInitialized: Boolean = false
        private set

    /**
     * Provides stylesheets should be applied on the embedded web-content.
     *
     * These stylesheets should not follow the JavaFX specific syntax
     * since they will be applied on simple web-pages.
     *
     * The main purpose of this is to make the internal web-content (e.g. the description in a Google Book's details)
     * fit to the UI-theme.
     *
     * @return list of stylesheet urls
     */
    open val embeddedWebStylesheets: List<String> get() = emptyList()

    /**
     * Applies the theme on the given [Scene]
     */
    abstract fun apply(scene: Scene)

    /**
     * Applies the theme on the given [Parent]
     */
    abstract fun apply(region: Parent)

    /**
     * Removes the theme from the given [Scene]
     */
    abstract fun deApply(scene: Scene)

    /**
     * Removes the theme from the given [Parent]
     */
    abstract fun deApply(region: Parent)

    /**
     * "Initializes" the theme.
     * Called when the theme is set as [default].
     *
     * **Shouldn't be called by other components!**
     * **If it is overridden by a subclass, the overriding method should also call this super function!**
     */
    @MustBeInvokedByOverriders
    open fun init() {
        isInitialized = true
    }

    /**
     * "Drops" the theme.
     * Called when the theme is replaced as a [default] theme.
     *
     * **Shouldn't be called by other components!**
     * **If it is overridden by a subclass, the overriding method should also call this super function!**
     */
    @MustBeInvokedByOverriders
    open fun destroy() {
        isInitialized = false
    }

    companion object {
        private val listeners = synchronizedList(mutableListOf<WeakReference<DefaultThemeListener>>())

        /**
         * Registers a [DefaultThemeListener], that will be notified when the [default] theme is changed.
         *
         * _Note: this method registers the listener **weakly**, so you have to have another reference for the
         * listener object, if you want to prevent it from being garbage collected!_
         */
        @JvmStatic
        fun registerListener(value: DefaultThemeListener): Boolean {
            value.onDefaultThemeChanged(null, default)
            return listeners.add(WeakReference(value))
        }

        /**
         * Returns the list of available themes.
         *
         * @see AvailableThemes
         */
        @JvmStatic
        val available get() = AvailableThemes

        /**
         * The global, default [Theme]
         */
        @JvmStatic
        var default: Theme by DefaultThemeDelegate()
    }

    fun interface DefaultThemeListener {
        fun onDefaultThemeChanged(oldTheme: Theme?, newTheme: Theme)
    }

    /**
     * For handling default-theme changing requests
     */
    private class DefaultThemeDelegate : ReadWriteProperty<Companion, Theme> {

        private var default: Theme? = null

        @Synchronized
        override fun getValue(thisRef: Companion, property: KProperty<*>): Theme {
            return default?.apply {
                if (!isInitialized)
                    init()
            } ?: get(InternalThemesConfig::class).defaultTheme
        }

        @Synchronized
        override fun setValue(thisRef: Companion, property: KProperty<*>, value: Theme) {
            if (default != value && default?.javaClass != value.javaClass) {
                notifyListeners(default, value)
                if (default?.isInitialized ?: false) default?.destroy()
                default = value
                value.init()
            }
        }

        private fun notifyListeners(oldTheme: Theme?, newTheme: Theme) {
            val iterator = listeners.iterator()
            while(iterator.hasNext()) {
                val weakReference = iterator.next()
                val listener = weakReference.get()
                listener?.onDefaultThemeChanged(oldTheme, newTheme) ?: iterator.remove()
            }
        }

    }
}