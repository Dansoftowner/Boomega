package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.appdata.Preferences
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object KeyBindings {

    private val CONFIG_KEY = Preferences.Key("default.key.bindings", KeyBindings::class.java) { this }

    val newEntryKeyBinding: KeyBinding =
        KeyBinding("newEntryKeyBinding", KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN))

    val restartApplicationKeyBinding: KeyBinding =
        KeyBinding("restartApplicationKeyBinding",KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN))

    val openDatabaseKeyBinding: KeyBinding =
        KeyBinding("openDatabaseKeyBinding", KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))

    val createDatabaseKeyBinding: KeyBinding =
        KeyBinding("createDatabaseKeyBinding", KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))

    val openDatabaseManagerKeyBinding: KeyBinding =
        KeyBinding("openDatabaseManagerKeyBinding", KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))

    val fullScreenKeyBinding: KeyBinding =
        KeyBinding("fullScreenKeyBinding",KeyCodeCombination(KeyCode.F11))

    val saveChangesKeyBinding: KeyBinding =
        KeyBinding("saveChangesKeyBinding", KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN))

    @JvmStatic
    fun loadFrom(preferences: Preferences) {
        preferences.get(CONFIG_KEY)
    }

    @JvmStatic
    fun writeTo(preferences: Preferences) {
        preferences.editor().put(CONFIG_KEY, this)
    }
}