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
private class NotificationsBox : Group() {

    private var vBox: VBox

    init {
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT)
        StackPane.setMargin(this, Insets(20.0))
        children.add(VBox(10.0).also {
            vBox = it
            it.alignment = Pos.CENTER_RIGHT
        })
    }

    fun pushItem(notificationNode: NotificationNode, duration: Duration?) {
        this.vBox.children.add(notificationNode)
        animatefx.animation.FadeIn(notificationNode).also {
            if (duration != null) {
                it.setOnFinished {
                    val animation = animatefx.animation.FadeOut(notificationNode).setDelay(duration)
                    animation.setOnFinished {
                        this.vBox.children.remove(notificationNode)
                    }
                    animation.play()
                }
            }
        }.play()
    }

    fun removeItem(notificationNode: NotificationNode) {
        animatefx.animation.FadeOut(notificationNode).also {
            it.setOnFinished {
                this.vBox.children.remove(notificationNode)
            }
        }.play()
    }
}