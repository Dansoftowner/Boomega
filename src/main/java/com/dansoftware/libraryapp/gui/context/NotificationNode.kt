package com.dansoftware.libraryapp.gui.context

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

internal
class NotificationNode(type: NotificationType, title: String, message: String) : Group(EntryPanel(type, title, message)) {

    enum class NotificationType(internal val icon: FontAwesomeIcon) {
        INFO(FontAwesomeIcon.INFO_CIRCLE),
        WARNING(FontAwesomeIcon.WARNING),
        ERROR(FontAwesomeIcon.RANDOM);
    }

    private class EntryPanel(type: NotificationType, text: String, message: String) : HBox(30.0) {
        init {
            styleClass.add("notification-node")
            children.add(StackPane(FontAwesomeIconView(type.icon, "50")))
            children.add(ContentPanel(text, message))
        }
    }

    private class ContentPanel(title: String, message: String) : VBox(2.0) {
        init {
            styleClass.add("content-panel")
            children.addAll(buildTitleLabel(title), buildMessageLabel(message))
        }

        private fun buildTitleLabel(text: String) = object : Label(text) {
            init {
                styleClass.add("title")
            }
        }

        private fun buildMessageLabel(text: String) = object : Label(text) {
            init {
                styleClass.add("message")
            }
        }

    }
}