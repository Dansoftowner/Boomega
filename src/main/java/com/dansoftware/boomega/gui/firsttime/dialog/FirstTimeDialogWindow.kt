package com.dansoftware.boomega.gui.firsttime.dialog

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality

/**
 * The [javafx.stage.Stage] that is used for showing a [FirstTimeDialog].
 *
 * @author Daniel Gyorffy
 */
class FirstTimeDialogWindow(firstTimeDialog: FirstTimeDialog) :
    BaseWindow<FirstTimeDialog>("window.firsttime.title", firstTimeDialog) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        isAlwaysOnTop = true
        isResizable = true
    }
}