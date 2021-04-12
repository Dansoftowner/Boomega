package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.config.Preferences
import javafx.stage.Stage
import javafx.stage.Window

class PreferencesActivity(private val preferences: Preferences) {

    fun show(owner: Window?) {
        PreferencesWindow(PreferencesView(preferences), owner).let(Stage::show)
    }
}