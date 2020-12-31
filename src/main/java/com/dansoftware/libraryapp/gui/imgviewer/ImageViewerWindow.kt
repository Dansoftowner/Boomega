package com.dansoftware.libraryapp.gui.imgviewer

import com.dansoftware.libraryapp.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * [javafx.stage.Stage] implementation for showing an [ImageViewer]
 *
 * @author Daniel Gyorffy
 */
class ImageViewerWindow(owner: Window?, content: ImageViewer) :
    BaseWindow<ImageViewer>("window.image.viewer", content) {

    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
    }
}