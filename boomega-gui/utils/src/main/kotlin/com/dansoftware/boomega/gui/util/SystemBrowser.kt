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

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.util.os.OsInfo.isLinux
import com.dansoftware.boomega.util.os.OsInfo.isMacOS
import com.dansoftware.boomega.util.os.OsInfo.isWindows
import java.awt.Desktop
import java.net.URL
import java.util.concurrent.ExecutorService

/**
 * SystemBrowser is a utility for communicating with the default browser of the system.
 *
 * @author Daniel Gyorffy
 */
object SystemBrowser {

    private val browserCore: BrowserCore

    init {
        browserCore = when {
            Desktop.isDesktopSupported() -> BaseBrowser()
            isWindows -> WindowsBrowser()
            isMacOS -> MacBrowser()
            isLinux -> LinuxBrowser()
            else -> NullBrowser()
        }
    }

    fun browse(url: String) {
        get(ExecutorService::class, "cachedExecutor").submit { browserCore.browse(url) }
    }

    private abstract class BrowserCore {
        abstract fun browse(url: String)
    }

    private class BaseBrowser : BrowserCore() {
        override fun browse(url: String) {
            Desktop.getDesktop().browse(URL(url).toURI())
        }
    }

    private class WindowsBrowser : BrowserCore() {
        override fun browse(url: String) {
            Runtime.getRuntime().also {
                it.exec("rundll32 url.dll,FileProtocolHandler $url")
            }
        }
    }

    private class MacBrowser : BrowserCore() {
        override fun browse(url: String) {
            Runtime.getRuntime().also {
                it.exec("open $url")
            }
        }
    }

    private class LinuxBrowser : BrowserCore() {
        override fun browse(url: String) {
            Runtime.getRuntime().also {
                it.exec("xdg-open $url")
            }
        }
    }

    private class NullBrowser : BrowserCore() {
        override fun browse(url: String) {
        }
    }
}