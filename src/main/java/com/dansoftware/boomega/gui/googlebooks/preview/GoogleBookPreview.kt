package com.dansoftware.boomega.gui.googlebooks.preview

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.api.EmptyContext
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.service.googlebooks.Volume
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.web.WebEngine
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
class GoogleBookPreview(private val context: Context, private val volume: Volume) : StackPane(), EmptyContext {

    private var webView: WebView?
    private var webEngine: WebEngine?

    init {
        styleClass.add("google-book-preview")
        webView = buildWebView()
        webEngine = webView!!.engine
        buildUI()
        loadContent()
    }

    private fun buildUI() {
        when {
            isServerReachable() -> children.add(webView)
            else -> children.add(buildOfflinePlaceHolder())
        }
    }

    private fun buildWebView() = WebView().apply {
        engine.setOnAlert {
            when (it.data) {
                ON_NOT_FOUND_ALERT -> {
                    clean()
                    children.setAll(buildVolumeNotFoundPlaceHolder())
                }
                else ->
                    context.showInformationDialog("", it.data) {}
            }
        }
    }

    private fun loadContent() {
        webEngine!!.loadContent(generateHTMLContent(volume), "text/html")
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
                        StackPane(MaterialDesignIconView(MaterialDesignIcon.ALERT_CIRCLE)),
                        Label(I18N.getValue(msg))
                    )
                )
            )
        }

    fun clean() {
        webEngine?.load("about:blank")
        children.clear()
        webView = null
        webEngine = null
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