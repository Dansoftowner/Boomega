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
package com.dansoftware.boomega.gui.app

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader.*
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.main.parseArguments
import com.sun.javafx.application.LauncherImpl
import javafx.application.Application
import javafx.stage.Stage

/**
 * The base [Application] implementation.
 */
abstract class BaseBoomegaApplication : Application() {

    /**
     * Returns the application-arguments in a [List].
     *
     * @return the list of arguments
     */
    val applicationArgs: List<String>
        get() = parameters.raw

    val launchedDatabase: DatabaseMeta? by lazy {
        parseArguments(applicationArgs)
    }

    final override fun start(primaryStage: Stage) {}

    /**
     * Should initialize and launch the javafx app
     */
    abstract override fun init()

    /**
     * Sends a message to the preloader
     *
     * @param i18n the non-internationalized message
     */
    protected fun notifyPreloader(i18n: String) {
        notifyPreloader(MessageNotification(i18n(i18n)))
    }

    /**
     * Sends progress-update to the preloader
     *
     * @param value the progress state between 0 and 1
     */
    protected fun progress(value: Double) {
        notifyPreloader(BoomegaPreloader.ProgressNotification(value))
    }

    /**
     * Sends hide-request to the preloader
     */
    protected fun hidePreloader() {
        notifyPreloader(HideNotification())
    }

    /**
     * Sends show-request to the preloader
     */
    protected fun showPreloader() {
        notifyPreloader(ShowNotification())
    }

    companion object {

        /**
         * Launches the base-application with the [BoomegaPreloader].
         *
         * @param appClass the class-reference to the [BaseBoomegaApplication] implementation
         * @param args     the application-arguments
         */
        @JvmStatic
        fun launchApp(appClass: Class<out BaseBoomegaApplication?>?, vararg args: String?) {
            LauncherImpl.launchApplication(appClass, BoomegaPreloader::class.java, args)
        }
    }
}