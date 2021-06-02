package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.util.os.OsInfo
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent

class KeyBinding(
    val id: String,
    val i18nTitle: String,
    val i18nDescription: String,
    val defaultKeyCombination: KeyCombination
) {

    val keyCombinationProperty: ObjectProperty<KeyCombination> =
        SimpleObjectProperty(defaultKeyCombination)
    val keyCombination: KeyCombination
        get() = keyCombinationProperty.get()

    constructor(
        id: String,
        i18nTitle: String,
        i18nDescription: String,
        winLinuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination
    ) : this(
        id,
        i18nTitle,
        i18nDescription,
        when {
            OsInfo.isMac() -> macKeyCombination
            else -> winLinuxKeyCombination
        }
    )

    constructor(
        id: String,
        i18nTitle: String,
        i18nDescription: String,
        winKeyCombination: KeyCombination,
        linuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination,
    ) : this(
        id,
        i18nTitle,
        i18nDescription,
        when {
            OsInfo.isLinux() -> linuxKeyCombination
            OsInfo.isMac() -> macKeyCombination
            else -> winKeyCombination
        }
    )

    fun match(keyEvent: KeyEvent) = keyCombination.match(keyEvent)
}