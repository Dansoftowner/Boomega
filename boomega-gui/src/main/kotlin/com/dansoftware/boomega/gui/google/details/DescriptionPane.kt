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
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.css.*
import javafx.css.converter.StringConverter
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.web.WebView

class DescriptionPane : StackPane() {

    private var content: Node
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    private val description: StringProperty = SimpleStringProperty()

    private val webViewCssProperty: StyleableStringProperty = SimpleStyleableStringProperty(webViewCSSPropertyMetaData)

    private val webView = WebView().apply {
        pageFill = Color.TRANSPARENT
        webViewCssProperty.addListener { _, _, webViewCssResource ->
            engine.userStyleSheetLocation = webViewCssResource
        }
    }

    private val placeHolder = Label(i18n("google.book.description.empty"))

    init {
        styleClass.add("description-pane")
        prefWidth = 500.0
        initPlaceHolderPolicy()
    }

    private fun initPlaceHolderPolicy() {
        description.addListener { _, _, newDesc ->
            content = newDesc?.let {
                webView.apply { engine.loadContent(newDesc, "text/html") }
            } ?: placeHolder
        }
    }

    fun descriptionProperty() = description

    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return super.getCssMetaData() + listOf(webViewCSSPropertyMetaData)
    }

    companion object {
        private val webViewCSSPropertyMetaData =
            object : CssMetaData<DescriptionPane, String>("-web-view-css", StringConverter.getInstance()) {
                override fun isSettable(styleable: DescriptionPane?): Boolean {
                    return styleable?.webViewCssProperty?.isBound?.not() ?: false
                }

                override fun getStyleableProperty(styleable: DescriptionPane?): StyleableProperty<String> {
                    return styleable!!.webViewCssProperty
                }
            }
    }
}