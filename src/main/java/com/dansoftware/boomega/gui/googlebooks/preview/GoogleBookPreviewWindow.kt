package com.dansoftware.boomega.gui.googlebooks.preview

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.Window
import javafx.stage.WindowEvent

/**
 * The [javafx.stage.Stage] implementation used for showing a [GoogleBookPreview]
 *
 * @author Daniel Gyorffy
 */
class GoogleBookPreviewWindow(owner: Window?, content: GoogleBookPreview) :
    BaseWindow<GoogleBookPreview>("window.google.book.preview.title", content) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, object : EventHandler<WindowEvent> {
            override fun handle(event: WindowEvent?) {
                content.clean()
                removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this)
            }
        })
    }
}