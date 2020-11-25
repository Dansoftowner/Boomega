package com.dansoftware.libraryapp.gui.firsttime.dialog

import com.dansoftware.libraryapp.appdata.Preferences
import java.util.*

/**
 * Used for showing [FirstTimeDialog] with a [FirstTimeDialogWindow].
 *
 * @author Daniel Gyorffy
 */
class FirstTimeDialogActivity(preferences: Preferences) {
    private val preferences: Preferences
    fun show() {
        val window = FirstTimeDialogWindow(FirstTimeDialog(preferences))
        window.showAndWait()
    }

    init {
        this.preferences = Objects.requireNonNull(preferences, "Preferences shouldn't be null")
    }
}