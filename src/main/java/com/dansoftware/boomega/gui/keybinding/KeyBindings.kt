package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.appdata.Preferences
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object KeyBindings {

    private val CONFIG_KEY = Preferences.Key("default.key.bindings", KeyBindings::class.java) { this }

    val newEntryKeyBinding: KeyBinding =
        KeyBinding(
            "newEntryKeyBinding",
            "preferences.keybindings.new_entry",
            "preferences.keybindings.new_entry.desc",
            KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN)
        )

    val restartApplicationKeyBinding: KeyBinding =
        KeyBinding(
            "restartApplicationKeyBinding",
            "preferences.keybindings.restart",
            "preferences.keybindings.restart.desc",
            KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        )

    val openDatabaseKeyBinding: KeyBinding =
        KeyBinding(
            "openDatabaseKeyBinding",
            "preferences.keybindings.open_database",
            "preferences.keybindings.open_database.desc",
            KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
        )

    val createDatabaseKeyBinding: KeyBinding =
        KeyBinding(
            "createDatabaseKeyBinding",
            "preferences.keybindings.create_database",
            "preferences.keybindings.create_database.desc",
            KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
        )

    val openDatabaseManagerKeyBinding: KeyBinding =
        KeyBinding(
            "openDatabaseManagerKeyBinding",
            "preferences.keybindings.open_database_manager",
            "preferences.keybindings.open_database_manager.desc",
            KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
        )

    val fullScreenKeyBinding: KeyBinding =
        KeyBinding(
            "fullScreenKeyBinding",
            "preferences.keybindings.full_screen",
            "preferences.keybindings.full_screen.desc",
            KeyCodeCombination(KeyCode.F11)
        )

    val saveChangesKeyBinding: KeyBinding =
        KeyBinding(
            "saveChangesKeyBinding",
            "preferences.keybindings.save_changes",
            "preferences.keybindings.save_changes.desc",
            KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        )

    @JvmStatic
    fun allKeyBindings(): List<KeyBinding> =
        javaClass.declaredFields
            .filter { it.type == KeyBinding::class.java }
            .map { it.get(this) as KeyBinding }
            .toList()

    @JvmStatic
    fun loadFrom(preferences: Preferences) {
        preferences.get(CONFIG_KEY)
    }

    @JvmStatic
    fun writeTo(preferences: Preferences) {
        preferences.editor().put(CONFIG_KEY, this)
    }
}