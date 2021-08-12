/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.i18n.i18n
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object KeyBindings {

    private val CONFIG_KEY = PreferenceKey(
        "default.key.bindings",
        KeyBindings::class.java,
        KeyBindingsAdapter()
    ) { this }

    val newEntry: KeyBinding =
        KeyBinding(
            id = "newEntryKeyBinding",
            title = i18n("action.new_entry"),
            description = { i18n("preferences.keybindings.new_entry.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.N,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN)
        )

    val restartApplication: KeyBinding =
        KeyBinding(
            id = "restartApplicationKeyBinding",
            title = i18n("action.restart"),
            description = { i18n("preferences.keybindings.restart.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.R,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.R, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN),
        )

    val openDatabase: KeyBinding =
        KeyBinding(
            id = "openDatabaseKeyBinding",
            title = i18n("action.open_database"),
            description = { i18n("preferences.keybindings.open_database.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.O,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
        )

    val createDatabase: KeyBinding =
        KeyBinding(
            id = "createDatabaseKeyBinding",
            title = i18n("action.create_database"),
            description = { i18n("preferences.keybindings.create_database.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.C,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
        )

    val openDatabaseManager: KeyBinding =
        KeyBinding(
            id = "openDatabaseManagerKeyBinding",
            title = i18n("action.open_database_manager"),
            description = { i18n("preferences.keybindings.open_database_manager.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.M,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN),
        )

    val fullScreen: KeyBinding =
        KeyBinding(
            id = "fullScreenKeyBinding",
            title = i18n("action.full_screen"),
            description = { i18n("preferences.keybindings.full_screen.desc") },
            defaultKeyCombination = KeyCodeCombination(KeyCode.F11)
        )

    val saveChanges: KeyBinding =
        KeyBinding(
            id = "saveChangesKeyBinding",
            title = i18n("action.save_changes"),
            description = { i18n("preferences.keybindings.save_changes.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN)
        )

    val deleteRecord: KeyBinding =
        KeyBinding(
            id = "deleteRecordKeyBinding",
            title = i18n("preferences.keybindings.delete_record"),
            description = { i18n("preferences.keybindings.delete_record.desc") },
            defaultKeyCombination = KeyCodeCombination(KeyCode.DELETE)
        )

    val copyRecord: KeyBinding =
        KeyBinding(
            id = "copyRecordKeyBinding",
            title = i18n("preferences.keybindings.copy_record"),
            description = { i18n("preferences.keybindings.copy_record.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN)
        )

    val cutRecord: KeyBinding =
        KeyBinding(
            id = "cutRecordKeyBinding",
            title = i18n("preferences.keybindings.cut_record"),
            description = { i18n("preferences.keybindings.cut_record.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.X, KeyCombination.META_DOWN)
        )

    val pasteRecord: KeyBinding =
        KeyBinding(
            id = "pasteRecordKeyBinding",
            title = i18n("preferences.keybindings.paste_record"),
            description = { i18n("preferences.keybindings.paste_record.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.V, KeyCombination.META_DOWN)
        )

    val duplicateRecord: KeyBinding =
        KeyBinding(
            id = "duplicateRecordKeyBinding",
            title = i18n("preferences.keybindings.duplicate_record"),
            description = { i18n("preferences.keybindings.duplicate_record.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.D, KeyCombination.META_DOWN)
        )

    val findRecord: KeyBinding =
        KeyBinding(
            id = "findRecordKeyBinding",
            title = i18n("preferences.keybindings.find_record"),
            description = { i18n("preferences.keybindings.find_record.desc") },
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN)
        )

    val openSettings: KeyBinding =
        KeyBinding(
            id = "openSettingsKeyBinding",
            title = i18n("action.settings"),
            description = { i18n("preferences.keybindings.open_settings.desc") },
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
        )

    val refreshPage: KeyBinding =
        KeyBinding(
            id = "refreshPageKeyBinding",
            title = i18n("page.reload"),
            description = { "" },
            defaultKeyCombination = KeyCodeCombination(KeyCode.F5)
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