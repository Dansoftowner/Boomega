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

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.gui.util.SystemBrowser
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
open class WebsiteHyperLink @JvmOverloads constructor(
    text: String,
    url: String? = null
) : Hyperlink(), EventHandler<ActionEvent> {

    private val url: StringProperty

    init {
        this.url = SimpleStringProperty(url)
        this.tooltip = HyperlinkTooltip(this)
        this.text = text
        this.onAction = this
    }

    override fun handle(event: ActionEvent) {
        url.get()?.let { SystemBrowser.browse(it) }
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