package com.dansoftware.boomega.gui.imgviewer

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * [javafx.stage.Stage] implementation for showing an [ImageViewer]
 *
 * @author Daniel Gyorffy
 */
class ImageViewerWindow(owner: Window?, content: ImageViewer) :
    BaseWindow("window.image.viewer", content, { content.context }) {

    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
    }
}