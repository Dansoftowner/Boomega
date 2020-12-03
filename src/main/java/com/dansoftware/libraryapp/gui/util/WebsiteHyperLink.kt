package com.dansoftware.libraryapp.gui.util

import com.dansoftware.libraryapp.util.SystemBrowser
import com.dansoftware.libraryapp.util.SystemBrowser.Companion.isSupported
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Hyperlink
import javafx.scene.control.Tooltip

/**
 * A [WebsiteHyperLink] is a [Hyperlink] implementation that can
 * open websites in the default browser easily.
 *
 *
 *
 * It also has a [Tooltip] that shows the url.
 *
 * @author Daniel Gyorffy
 */
class WebsiteHyperLink(
    text: String,
    url: String
) : Hyperlink(), EventHandler<ActionEvent> {

    private val url: StringProperty

    init {
        this.url = SimpleStringProperty(url)
        this.tooltip = HyperlinkTooltip(this)
        this.text = text
        this.onAction = this
    }

    override fun handle(event: ActionEvent) {
        if (isSupported()) {
            val systemBrowser = SystemBrowser()
            systemBrowser.browse(url.get())
        }
    }

    fun getUrl(): String {
        return url.get()
    }

    fun urlProperty(): StringProperty = url

    /**
     * The tooltip implementation
     */
    private class HyperlinkTooltip(parent: WebsiteHyperLink) : Tooltip() {
        init {
            textProperty().bind(parent.url)
        }
    }
}