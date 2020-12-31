package com.dansoftware.libraryapp.gui.googlebooks.preview

import com.dansoftware.libraryapp.googlebooks.Volume
import javafx.stage.Window

/**
 * Used for showing a [GoogleBookPreview] with a [GoogleBookPreviewWindow]
 *
 * @author Daniel Gyorffy
 */
class GoogleBookPreviewActivity(private val volume: Volume, private val owner: Window? = null) {
    fun show() = GoogleBookPreviewWindow(owner, GoogleBookPreview(volume)).show()
}