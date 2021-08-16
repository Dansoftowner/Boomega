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

package com.dansoftware.boomega.gui.base

import com.dansoftware.boomega.gui.util.icon
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.function.Consumer

/**
 * A [NotificationNode] is a gui element that represents a notification-tile
 *
 * @author Daniel Gyorffy
 */
class NotificationNode @JvmOverloads constructor(
    type: NotificationType,
    title: String,
    message: String?,
    hyperLinks: Array<Hyperlink>? = null,
    closeAction: Consumer<NotificationNode>
) : Group(EntryPanel(type, title, message, hyperLinks, closeAction)) {

    /**
     * Represents the three notification type
     */
    enum class NotificationType(val iconStyleClass: String) {
        INFO("info-icon"),
        WARNING("warning-circle-icon"),
        ERROR("close-circle-icon");
    }

    /**
     * It holds the icon, [ContentPanel], and a close button
     */
    private class EntryPanel(
        type: NotificationType,
        text: String,
        message: String?,
        hyperLinks: Array<Hyperlink>?,
        private val closeAction: Consumer<NotificationNode>
    ) : HBox(8.0) {

        init {
            styleClass.add("notification-node")
            children.add(icon(type.iconStyleClass))
            children.add(ContentPanel(text, message, hyperLinks))
            children.add(buildCloseButton())
        }

        private fun buildCloseButton(): Node = icon("close-icon").apply {
            isVisible = false

            this@EntryPanel.setOnMouseEntered { isVisible = true }
            this@EntryPanel.setOnMouseExited { isVisible = false }

            setOnMouseClicked { event ->
                when (event.button) {
                    MouseButton.PRIMARY ->
                        closeAction.accept(this@EntryPanel.parent as NotificationNode)
                }
            }
        }
    }

    /**
     * Holds the title and the message labels.
     */
    private class ContentPanel(title: String, message: String?, hyperLinks: Array<Hyperlink>?) : StackPane() {
        init {
            children.add(Group(VBox(2.0).apply {
                styleClass.add("content-panel")
                children.add(buildTitleLabel(title))
                message.takeIf { it?.isBlank()?.not() ?: false }?.let { children.add(buildMessageLabel(message!!)) }
                hyperLinks.takeIf { it?.isNotEmpty() ?: false}?.let { children.add(HBox(2.0, *hyperLinks!!)) }
            }))
        }

        private fun buildTitleLabel(text: String) = Label(text).apply { styleClass.add("title") }

        private fun buildMessageLabel(text: String) = Label(text).apply { styleClass.add("message") }
    }
}