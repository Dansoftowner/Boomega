package com.dansoftware.libraryapp.gui.firsttime.dialog

import com.dansoftware.libraryapp.appdata.Preferences
import java.util.*

/**
 * Used for showing [FirstTimeDialog] with a [FirstTimeDialogWindow].
 *
 * @author Daniel Gyorffy
 */
class FirstTimeDialogActivity(private val preferences: Preferences) {
    init {
        Objects.requireNonNull(preferences, "Preferences shouldn't be null")
    }

    fun show() {
        val window = FirstTimeDialogWindow(FirstTimeDialog(preferences))
        window.showAndWait()
    }
}