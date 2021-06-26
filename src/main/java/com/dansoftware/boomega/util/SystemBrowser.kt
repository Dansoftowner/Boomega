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

package com.dansoftware.boomega.util

import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.dansoftware.boomega.util.os.OsInfo
import java.awt.Desktop
import java.net.URL

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
            OsInfo.isWindows() -> WindowsBrowser()
            OsInfo.isMac() -> MacBrowser()
            OsInfo.isLinux() -> LinuxBrowser()
            else -> NullBrowser()
        }
    }

    fun browse(url: String) {
        CachedExecutor.submit { browserCore.browse(url) }
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