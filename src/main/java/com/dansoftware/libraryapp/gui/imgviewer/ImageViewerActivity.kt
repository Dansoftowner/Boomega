package com.dansoftware.libraryapp.gui.imgviewer

import javafx.scene.image.Image
import javafx.stage.Window

/**
 * Used for showing an [ImageViewer] with an [ImageViewerWindow].
 *
 * @author Daniel Gyorffy
 */
class ImageViewerActivity(private val image: Image, private val owner: Window? = null) {
    fun show() = ImageViewerWindow(owner, ImageViewer(image)).show()
}