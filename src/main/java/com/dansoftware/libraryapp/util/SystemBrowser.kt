package com.dansoftware.libraryapp.util

import java.awt.Desktop
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

/**
 * SystemBrowser is a utility for communicating with the default browser of the system.
 *
 * @author Daniel Gyorffy
 */
class SystemBrowser {
    companion object {
        @JvmStatic
        fun isSupported(): Boolean =
            Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
    }

    fun browse(url: String) =
        try {
            Desktop.getDesktop().browse(URL(url).toURI())
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
}