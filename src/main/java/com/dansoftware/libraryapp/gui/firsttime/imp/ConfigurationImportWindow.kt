package com.dansoftware.libraryapp.gui.firsttime.imp

import com.dansoftware.libraryapp.gui.window.LibraryAppStage
import javafx.stage.Modality

/**
 * A [javafx.stage.Stage] optimized for a [ConfigurationImportView].
 *
 * @author Daniel Gyorffy
 */
class ConfigurationImportWindow(view: ConfigurationImportView) :
    LibraryAppStage<ConfigurationImportView>("window.config.import.title", view) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        isAlwaysOnTop = true
        isResizable = false
        width = 500.0
        height = 250.0
    }
}