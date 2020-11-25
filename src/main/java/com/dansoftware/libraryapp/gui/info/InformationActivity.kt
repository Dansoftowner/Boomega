package com.dansoftware.libraryapp.gui.info

import com.dansoftware.libraryapp.gui.context.Context
import javafx.geometry.Pos
import javafx.scene.layout.StackPane

/**
 * An InformationActivity is used for showing the information of the application.
 *
 * @author Daniel Gyorffy
 */
class InformationActivity(private val context: Context) {
    fun show() {
        val view = InformationView(context)
        context.showOverlay(view, false)
        StackPane.setAlignment(view, Pos.CENTER)
    }
}