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

@file:JvmName("Main")

package com.dansoftware.boomega.main

import com.dansoftware.boomega.exception.UncaughtExceptionHandler
import com.dansoftware.boomega.gui.app.BaseApplication
import com.dansoftware.boomega.gui.app.BoomegaApp
import com.dansoftware.boomega.instance.ApplicationInstanceService

fun main(args: Array<String>) {
    init(args)
    launch(args)
}

private fun init(args: Array<String>) {
    PropertiesSetup.setupSystemProperties()
    Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler())
    ApplicationInstanceService.open(args)
}

private fun launch(args: Array<String>) {
    BaseApplication.launchApp(BoomegaApp::class.java, *args)
}