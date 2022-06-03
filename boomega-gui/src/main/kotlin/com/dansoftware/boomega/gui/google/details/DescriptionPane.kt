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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.rest.google.books.Volume
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.css.*
import javafx.css.converter.StringConverter
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.web.WebView

/**
 * Responsible for displaying a Google Book's description in an easy-to-read format.
 *
 * Since it uses a [WebView] for displaying the Google Book's description, it has a
 * CSS property called `-web-view-user-stylesheet` that allows to configure a custom
 * `user-stylesheet` for the web-view (*to make the web-view synchronizable with the
 * UI-theme*).
 */
class DescriptionPane : StackPane() {

    private var content: Node
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    /**
     * The observable value representing the current volume that's description is displayed
     */
    private val volume: ObjectProperty<Volume> = SimpleObjectProperty()

    /**
     * The observable value representing the actual HTML description text
     */
    private val description: ReadOnlyStringProperty = SimpleStringProperty().apply {
        bind(Bindings.createStringBinding({ volume.get()?.volumeInfo?.description }, volume))
    }

    /**
     * The observable value representing the WebView user-stylesheet configured in the current JavaFX CSS scope
     */
    private val webViewUserStylesheet: StyleableStringProperty = SimpleStyleableStringProperty(WEB_VIEW_USER_STYLESHEET)

    /**
     * The [WebView] responsible for displaying the HTML content
     */
    private val webView = WebView().apply {
        engine.userStyleSheetLocationProperty().bind(webViewUserStylesheet)
    }

    /**
     * The place-holder displayed in place of the web-view
     */
    private val placeHolder = Label(i18n("google.book.description.empty"))

    init {
        styleClass.add("description-pane")
        prefWidth = 500.0
        description.addListener { _, _, newDesc -> updateContent(newDesc) }
    }

    fun volumeProperty() = volume

    /**
     * Fits the appropriate content into the description pane:
     * - the [webView] if the description is not null
     * - The [placeHolder] if no description is available
     */
    private fun updateContent(newDescription: String?) {
        content = newDescription?.let {
            webView.load(it)
            webView
        } ?: run {
            webView.clear()
            placeHolder
        }
    }

    private fun WebView.load(html: String) {
        engine.loadContent(html, "text/html")
    }

    private fun WebView.clear() {
        engine.loadContent(null)
    }

    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return super.getCssMetaData() + listOf(WEB_VIEW_USER_STYLESHEET)
    }

    companion object StyleableProperties {

        /**
         * Represents the JavaFX CSS property available for the [DescriptionPane] that allows to configure the [webView]'s
         * `user-stylesheet`.
         */
        private val WEB_VIEW_USER_STYLESHEET =
            object : CssMetaData<DescriptionPane, String>("-web-view-user-stylesheet", StringConverter.getInstance()) {
                override fun isSettable(styleable: DescriptionPane?): Boolean {
                    return styleable?.webViewUserStylesheet?.isBound?.not() ?: false
                }

                override fun getStyleableProperty(styleable: DescriptionPane?): StyleableProperty<String> {
                    return styleable!!.webViewUserStylesheet
                }
            }
    }
}