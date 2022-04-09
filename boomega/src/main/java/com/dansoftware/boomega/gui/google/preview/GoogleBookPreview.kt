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

package com.dansoftware.boomega.gui.google.preview

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.web.WebView
import org.jetbrains.annotations.Nls
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.CookieHandler
import java.net.CookieManager
import java.util.*

/**
 * Used for showing a Google Book's preview content.
 *
 * @author Daniel Gyorffy
 */
class GoogleBookPreview(private val context: Context, val volume: Volume) : BorderPane() {

    private val webView: WebView

    init {
        styleClass.add("google-book-preview")
        top = GoogleBookPreviewToolbar(context, this)
        webView = buildWebView()
        loadContent()
    }

    private fun buildWebView() = WebView().apply {
        engine.setOnError {
            logger.error("Web error detected", it.exception)
        }
        engine.setOnAlert {
            when (it.data) {
                ON_NOT_FOUND_ALERT -> center = buildVolumeNotFoundPlaceHolder()
                else -> context.showInformationDialog("", it.data) {}
            }
        }
    }

    fun reload() {
        center = null
        loadContent()
    }

    private fun loadContent() {
        if (center !is WebView) {
            center = when {
                isServerReachable() ->
                    webView.apply {
                        engine.loadContent(generateHTMLContent(volume), "text/html")
                    }
                else -> buildOfflinePlaceHolder()
            }
        }
    }

    private fun buildVolumeNotFoundPlaceHolder() =
        buildPlaceHolder("google.book.preview.notavailable")

    private fun buildOfflinePlaceHolder() =
        buildPlaceHolder("google.book.preview.offline")

    private fun buildPlaceHolder(@Nls msg: String) =
        StackPane().apply {
            styleClass.add("place-holder")
            children.add(
                Group(
                    HBox(
                        10.0,
                        StackPane(icon("warning-circle-icon")),
                        Label(I18N.getValue(msg))
                    )
                )
            )
        }

    fun clean() {
        webView.engine.load("about:blank")
        children.clear()
        CookieHandler.setDefault(CookieManager())
    }

    private fun isServerReachable(): Boolean =
        com.dansoftware.boomega.util.isServerReachable(GOOGLE_BOOKS_JS_API_LOCATION)

    /**
     * See [google books developer's guide](https://developers.google.com/books/docs/viewer/developers_guide)
     */
    private fun generateHTMLContent(volume: Volume): String =
        """
<!DOCTYPE html "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title></title>
    <script type="text/javascript" src="$GOOGLE_BOOKS_JS_API_LOCATION"></script>
    <script type="text/javascript">
      google.books.load({"language": "${Locale.getDefault().language}"});

      function onNotFound() {
          alert('$ON_NOT_FOUND_ALERT')
      }

      function initialize() {
        var viewer = new google.books.DefaultViewer(document.getElementById('viewerCanvas'));
        viewer.load('${volume.id}', onNotFound);
      }

      google.books.setOnLoadCallback(initialize);
    </script>
  </head>
  <body style='overflow-x: hidden; overflow-y: hidden;'>
    <div id="viewerCanvas" style="resize: horizontal; height: 98vh;"></div>
  </body>
</html>
    """

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBookPreview::class.java)
        private const val GOOGLE_BOOKS_JS_API_LOCATION = "https://www.google.com/books/jsapi.js"
        private const val ON_NOT_FOUND_ALERT = "#"
    }
}