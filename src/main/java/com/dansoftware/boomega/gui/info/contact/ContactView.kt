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

package com.dansoftware.boomega.gui.info.contact

import com.dansoftware.boomega.gui.context.TitledOverlayBox
import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class ContactOverlay() : TitledOverlayBox(
    I18N.getValues().getString("contact.view.title"),
    MaterialDesignIconView(MaterialDesignIcon.CONTACT_MAIL),
    ContactView(),
    false,
    false
)

class ContactView() : VBox(5.0) {

    init {
        this.padding = Insets(5.0)
        this.styleClass.add(JMetroStyleClass.BACKGROUND)
        this.children.apply {
            add(buildEntry(MaterialDesignIcon.EMAIL, ContactValues.EMAIL))
            add(buildEntry(MaterialDesignIcon.TWITTER, ContactValues.TWITTER))
            add(buildEntry(MaterialDesignIcon.GITHUB_CIRCLE, ContactValues.GITHUB))
        }
    }

    private fun buildEntry(icon: MaterialDesignIcon, content: String): Node =
        buildEntry(MaterialDesignIconView(icon), HighlightableLabel(content))

    private fun buildEntry(icon: Node, content: Node): Node =
        HBox(5.0).apply {
            children.add(icon)
            children.add(content)
        }

}