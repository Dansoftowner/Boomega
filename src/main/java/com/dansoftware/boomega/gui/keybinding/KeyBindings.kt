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
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

object KeyBindings {

    private val CONFIG_KEY = PreferenceKey(
        "default.key.bindings",
        KeyBindings::class.java,
        KeyBindingsAdapter()
    ) { this }

    val newEntryKeyBinding: KeyBinding =
        KeyBinding(
            id = "newEntryKeyBinding",
            i18nTitle = "action.new_entry",
            i18nDescription = "preferences.keybindings.new_entry.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.N,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN)
        )

    val restartApplicationKeyBinding: KeyBinding =
        KeyBinding(
            id = "restartApplicationKeyBinding",
            i18nTitle = "action.restart",
            i18nDescription = "preferences.keybindings.restart.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.R,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.R, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN),
        )

    val openDatabaseKeyBinding: KeyBinding =
        KeyBinding(
            id = "openDatabaseKeyBinding",
            i18nTitle = "action.open_database",
            i18nDescription = "preferences.keybindings.open_database.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.O,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
        )

    val createDatabaseKeyBinding: KeyBinding =
        KeyBinding(
            id = "createDatabaseKeyBinding",
            i18nTitle = "action.create_database",
            i18nDescription = "preferences.keybindings.create_database.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.C,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
        )

    val openDatabaseManagerKeyBinding: KeyBinding =
        KeyBinding(
            id = "openDatabaseManagerKeyBinding",
            i18nTitle = "action.open_database_manager",
            i18nDescription = "preferences.keybindings.open_database_manager.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.M,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN),
        )

    val fullScreenKeyBinding: KeyBinding =
        KeyBinding(
            id = "fullScreenKeyBinding",
            i18nTitle = "action.full_screen",
            i18nDescription = "preferences.keybindings.full_screen.desc",
            defaultKeyCombination = KeyCodeCombination(KeyCode.F11)
        )

    val saveChangesKeyBinding: KeyBinding =
        KeyBinding(
            id = "saveChangesKeyBinding",
            i18nTitle = "action.save_changes",
            i18nDescription = "preferences.keybindings.save_changes.desc",
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN)
        )

    val deleteRecordKeyBinding: KeyBinding =
        KeyBinding(
            id = "deleteRecordKeyBinding",
            i18nTitle = "preferences.keybindings.delete_record",
            i18nDescription = "preferences.keybindings.delete_record.desc",
            defaultKeyCombination = KeyCodeCombination(KeyCode.DELETE)
        )

    val copyRecordKeyBinding: KeyBinding =
        KeyBinding(
            id = "copyRecordKeyBinding",
            i18nTitle = "preferences.keybindings.copy_record",
            i18nDescription = "preferences.keybindings.copy_record.desc",
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN)
        )

    val cutRecordKeyBinding: KeyBinding =
        KeyBinding(
            id = "cutRecordKeyBinding",
            i18nTitle = "preferences.keybindings.cut_record",
            i18nDescription = "preferences.keybindings.cut_record.desc",
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.X, KeyCombination.META_DOWN)
        )

    val pasteRecordKeyBinding: KeyBinding =
        KeyBinding(
            id = "pasteRecordKeyBinding",
            i18nTitle = "preferences.keybindings.paste_record",
            i18nDescription = "preferences.keybindings.paste_record.desc",
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.V, KeyCombination.META_DOWN)
        )

    val findRecordKeyBinding: KeyBinding =
        KeyBinding(
            id = "findRecordKeyBinding",
            i18nTitle = "preferences.keybindings.find_record",
            i18nDescription = "preferences.keybindings.find_record.desc",
            winLinuxKeyCombination = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
            macKeyCombination = KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN)
        )

    val openSettingsKeyBinding: KeyBinding =
        KeyBinding(
            id = "openSettingsKeyBinding",
            i18nTitle = "action.settings",
            i18nDescription = "preferences.keybindings.open_settings.desc",
            winLinuxKeyCombination = KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN
            ),
            macKeyCombination = KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN)
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