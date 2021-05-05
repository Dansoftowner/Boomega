package com.dansoftware.boomega.gui.googlebooks.preview

import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.stage.Window

/**
 * Used for showing a [GoogleBookPreview] with a [GoogleBookPreviewWindow]
 *
 * @author Daniel Gyorffy
 */
class GoogleBookPreviewActivity(private val volume: Volume, private val owner: Window? = null) {
    fun show() = GoogleBookPreviewWindow(owner, GoogleBookPreview(volume)).show()
}