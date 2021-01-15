package com.dansoftware.libraryapp.gui.info

import com.dansoftware.libraryapp.gui.context.Context

/**
 * An InformationActivity is used for showing the information of the application.
 *
 * @author Daniel Gyorffy
 */
class InformationActivity(private val context: Context) {
    fun show() {
        context.showOverlay(InformationViewOverlay(context), false)
    }
}