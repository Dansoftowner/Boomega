package com.dansoftware.libraryapp.gui.googlebooks.tile

import com.dansoftware.libraryapp.googlebooks.Volume
import javafx.scene.Group
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class GoogleBookTile(volume: Volume) : HBox(5.0, Thumbnail(volume), Info(volume)) {

    private class Thumbnail(volume: Volume) : StackPane() {

    }

    private class Info(volume: Volume) : StackPane() {

        init {
            children.add(Group(buildVBox(volume)))
        }

        private fun buildVBox(volume: Volume): VBox =
            VBox(3.0).also {

            }
    }
}