package com.dansoftware.libraryapp.util

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
        ExploitativeExecutor.submit { browserCore.browse(url) }
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