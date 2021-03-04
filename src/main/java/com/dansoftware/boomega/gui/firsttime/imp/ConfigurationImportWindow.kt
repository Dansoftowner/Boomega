package com.dansoftware.boomega.gui.firsttime.imp

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality

/**
 * A [javafx.stage.Stage] optimized for a [ConfigurationImportView].
 *
 * @author Daniel Gyorffy
 */
class ConfigurationImportWindow(view: ConfigurationImportView) :
    BaseWindow("window.config.import.title", view, { view.context }) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        isAlwaysOnTop = true
        isResizable = false
        width = 500.0
        height = 250.0
    }
}