package com.dansoftware.libraryapp.gui.context

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration

/**
 * A [NotificationsBox] is a gui element that holds multiple [NotificationsBox] objects
 * together.
 *
 * @author Daniel Gyorffy
 */
internal class NotificationsBox : Group() {

    private var vBox: VBox

    init {
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT)
        StackPane.setMargin(this, Insets(5.0))
        children.add(VBox(10.0).also {
            vBox = it
            it.alignment = Pos.CENTER_RIGHT
        })
    }

    fun pushItem(notificationNode: NotificationNode, duration: Duration?) {
        this.vBox.children.add(notificationNode)
        animatefx.animation.FadeIn(notificationNode).play()
        if (duration != null) {
            val animation = animatefx.animation.FadeOut(notificationNode).setDelay(duration)
            animation.setOnFinished {
                this.vBox.children.remove(notificationNode)
            }
            animation.play()
        }
    }

    fun removeItem(notificationNode: NotificationNode) {
        val animation = animatefx.animation.FadeOut(notificationNode).also {
            it.setOnFinished {
                this.vBox.children.remove(notificationNode)
            }
        }
        animation.play()
    }
}