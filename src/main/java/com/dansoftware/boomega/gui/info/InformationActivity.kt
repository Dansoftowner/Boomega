package com.dansoftware.boomega.gui.info

import com.dansoftware.boomega.gui.api.Context

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