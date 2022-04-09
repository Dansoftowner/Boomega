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

package com.dansoftware.boomega.gui.info.contact

import com.dansoftware.boomega.gui.base.TitledOverlayBox
import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class ContactOverlay() : TitledOverlayBox(
    I18N.getValues().getString("contact.view.title"),
    icon("contact-mail-icon"),
    ContactView(),
    false,
    false
)

class ContactView() : VBox(5.0) {

    init {
        this.padding = Insets(5.0)
        this.styleClass.add(JMetroStyleClass.BACKGROUND)
        this.children.apply {
            add(buildEntry("email-icon", ContactValues.EMAIL))
            add(buildEntry("twitter-icon", ContactValues.TWITTER))
            add(buildEntry("github-icon", ContactValues.GITHUB))
        }
    }

    private fun buildEntry(iconStyleClass: String, content: String): Node =
        buildEntry(icon(iconStyleClass), HighlightableLabel(content))

    private fun buildEntry(icon: Node, content: Node): Node =
        HBox(5.0).apply {
            children.add(icon)
            children.add(content)
        }

}