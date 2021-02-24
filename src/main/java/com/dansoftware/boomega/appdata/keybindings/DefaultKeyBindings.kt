package com.dansoftware.boomega.appdata.keybindings

import com.dansoftware.boomega.appdata.Preferences
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object DefaultKeyBindings {

    private val CONFIG_KEY = Preferences.Key("default.key.bindings", DefaultKeyBindings::class.java) { this }

    val newEntryProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN))
    val restartApplicationProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN))
    val openDatabaseProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))
    val createDatabaseProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))
    val openDatabaseManagerProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))
    val fullScreenProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.F11))
    val saveChangesProperty =
        SimpleObjectProperty(KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN))

    var newEntry: KeyCodeCombination
        get() = newEntryProperty.get()
        set(value) = newEntryProperty.set(value)

    var restartApplication: KeyCodeCombination
        get() = restartApplicationProperty.get()
        set(value) = restartApplicationProperty.set(value)

    var openDatabase: KeyCodeCombination
        get() = openDatabaseProperty.get()
        set(value) = openDatabaseProperty.set(value)

    var createDatabase: KeyCodeCombination
        get() = createDatabaseProperty.get()
        set(value) = createDatabaseProperty.set(value)

    var openDatabaseManager: KeyCodeCombination
        get() = openDatabaseManagerProperty.get()
        set(value) = openDatabaseManagerProperty.set(value)

    var fullScreen: KeyCodeCombination
        get() = fullScreenProperty.get()
        set(value) = fullScreenProperty.set(value)

    var saveChanges: KeyCodeCombination
        get() = saveChangesProperty.get()
        set(value) = saveChangesProperty.set(value)

    @JvmStatic
    fun loadFrom(preferences: Preferences) {
        preferences.get(CONFIG_KEY).takeIf { it !== this }?.also {
            newEntry = it.newEntry
            restartApplication = it.restartApplication
            openDatabase = it.openDatabase
            createDatabase = it.createDatabase
            openDatabaseManager = it.openDatabaseManager
            fullScreen = it.fullScreen
        }
    }

    @JvmStatic
    fun writeTo(preferences: Preferences) {
        preferences.editor().put(CONFIG_KEY, this)
    }
}