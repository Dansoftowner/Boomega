package com.dansoftware.libraryapp.gui.firsttime.dialog

import com.dansoftware.libraryapp.gui.window.LibraryAppStage
import javafx.stage.Modality

/**
 * The [javafx.stage.Stage] that is used for showing a [FirstTimeDialog].
 *
 * @author Daniel Gyorffy
 */
class FirstTimeDialogWindow(firstTimeDialog: FirstTimeDialog) :
    LibraryAppStage<FirstTimeDialog>("window.firsttime.title", firstTimeDialog) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        isAlwaysOnTop = true
        isResizable = true
    }
}