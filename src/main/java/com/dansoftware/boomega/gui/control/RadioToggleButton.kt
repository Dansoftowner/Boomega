package com.dansoftware.boomega.gui.control

import javafx.scene.control.RadioButton

/**
 * The [RadioToggleButton] is a [RadioButton] styled as a [ToggleButton]
 */
class RadioToggleButton(text: String? = null) : RadioButton(text) {
    init {
        styleClass.remove("radio-button")
        styleClass.add("toggle-button")
    }
}