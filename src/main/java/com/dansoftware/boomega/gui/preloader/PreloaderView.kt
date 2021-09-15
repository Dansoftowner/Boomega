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

package com.dansoftware.boomega.gui.preloader

import com.dansoftware.boomega.gui.util.asCentered
import com.dansoftware.boomega.gui.util.onScenePresent
import com.dansoftware.boomega.gui.util.styleClass
import com.dansoftware.boomega.gui.util.vgrow
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class PreloaderView : VBox() {

    val progressProperty: DoubleProperty = SimpleDoubleProperty(-1.0)
    val messageProperty: StringProperty = SimpleStringProperty()

    init {
        styleClass.add("preloader-view")
        buildUI()
    }

    private fun buildUI() {
        children.add(Logo().asCentered().vgrow(Priority.ALWAYS))
        children.add(Label(System.getProperty("app.name")).styleClass("logo-label").asCentered())
        children.add(buildMessageLabel().asCentered())
        children.add(buildProgressBar())
    }

    private fun buildProgressBar() = ProgressBar().apply {
        progressProperty().bind(progressProperty)
        maxWidth = Double.MAX_VALUE
    }

    private fun buildMessageLabel() = Label().apply {
        textProperty().bind(messageProperty)
    }

    private class Logo() : ImageView() {
        init {
            styleClass.add("center-logo")
            playAnimation()
        }

        private fun playAnimation() {
            onScenePresent {
                animatefx.animation
                    .BounceIn(this)
                    .play()
            }
        }
    }
}