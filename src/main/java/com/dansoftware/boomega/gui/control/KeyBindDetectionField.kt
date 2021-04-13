package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.gui.util.asKeyCombination
import com.dansoftware.boomega.gui.util.isUndefined
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.Event
import javafx.scene.control.TextField
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import org.apache.commons.lang3.StringUtils

class KeyBindDetectionField(initial: KeyCombination) : TextField() {

    private val keyCombination: ObjectProperty<KeyCombination> = object : SimpleObjectProperty<KeyCombination>() {
        override fun invalidated() {
            this@KeyBindDetectionField.text = StringUtils.getIfEmpty(get().displayText, null)
        }
    }

    init {
        this.isEditable = false
        //TODO: tooltip
        this.keyCombination.set(initial)
        this.setOnContextMenuRequested(Event::consume)
        this.setOnKeyTyped(KeyEvent::consume)
        this.setOnKeyPressed { event ->
            event.consume()
            when {
                event.isUndefined().not() -> {
                    event.asKeyCombination()?.let {
                        keyCombination.set(it)
                    }
                }
            }
        }
    }

    fun keyCombinationProperty() = keyCombination
}