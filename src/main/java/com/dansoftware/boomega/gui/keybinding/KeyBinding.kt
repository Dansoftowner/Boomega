package com.dansoftware.boomega.gui.keybinding

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent

class KeyBinding(val id: String, val defaultKeyCombination: KeyCombination) {

    val keyCombinationProperty: ObjectProperty<KeyCombination> =
        SimpleObjectProperty(defaultKeyCombination)
    val keyCombination: KeyCombination
        get() = keyCombinationProperty.get()

    fun match(keyEvent: KeyEvent) = keyCombination.match(keyEvent)
}