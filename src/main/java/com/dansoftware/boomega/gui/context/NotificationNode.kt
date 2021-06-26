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

package com.dansoftware.boomega.gui.context

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

/**
 * A [NotificationNode] is a gui element that represents a notification-tile
 *
 * @author Daniel Gyorffy
 */
internal class NotificationNode(
    type: NotificationType,
    title: String,
    message: String?,
    closeAction: Consumer<NotificationNode>
) : Group(EntryPanel(type, title, message, closeAction)) {

    /**
     * Represents the three notification type
     */
    enum class NotificationType(internal val icon: FontAwesomeIcon) {
        INFO(FontAwesomeIcon.INFO_CIRCLE),
        WARNING(FontAwesomeIcon.WARNING),
        ERROR(FontAwesomeIcon.CLOSE);
    }

    /**
     * It holds the icon, [ContentPanel], and a close button
     */
    private class EntryPanel(
        type: NotificationType,
        text: String,
        message: String?,
        private val closeAction: Consumer<NotificationNode>
    ) : HBox(8.0) {
        init {
            styleClass.add("notification-node")
            children.add(StackPane().also {
                val icon = FontAwesomeIconView(type.icon)
                icon.font = Font.font(20.0)
                it.children.add(icon)
            })
            children.add(ContentPanel(text, message))
            children.add(buildCloseButton())
        }

        private fun buildCloseButton(): Node = MaterialDesignIconView(MaterialDesignIcon.CLOSE).also {
            it.styleClass.add("close-btn")
            it.isVisible = false
            this.setOnMouseEntered { _ -> it.isVisible = true }
            this.setOnMouseExited { _ -> it.isVisible = false }
            it.setOnMouseClicked { event ->
                when (event.button) {
                    MouseButton.PRIMARY -> closeAction.accept(this.parent as NotificationNode)
                    else -> {
                    }
                }
            }
        }
    }

    /**
     * Holds the title and the message labels.
     */
    private class ContentPanel(title: String, message: String?) : StackPane() {
        init {
            children.add(Group(VBox(2.0).also {
                it.styleClass.add("content-panel")
                it.children.add(buildTitleLabel(title))
                when {
                    StringUtils.isBlank(message).not() -> it.children.add(buildMessageLabel(message!!))
                }
            }))
        }

        private fun buildTitleLabel(text: String) = Label(text).also { it.styleClass.add("title") }
        private fun buildMessageLabel(text: String) = Label(text).also { it.styleClass.add("message") }
    }
}