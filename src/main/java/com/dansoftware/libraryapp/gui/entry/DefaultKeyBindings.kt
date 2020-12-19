package com.dansoftware.libraryapp.gui.entry

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object DefaultKeyBindings {
    val NEW_ENTRY = KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN)
    val RESTART_APPLICATION = KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
    val OPEN_DATABASE = KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
    val CREATE_DATABASE = KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
    val OPEN_DATABASE_MANAGER = KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)
}