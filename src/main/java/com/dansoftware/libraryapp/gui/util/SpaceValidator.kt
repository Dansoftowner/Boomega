package com.dansoftware.libraryapp.gui.util

import javafx.scene.control.TextFormatter
import org.apache.commons.lang3.StringUtils

/**
 * A SpaceValidator can be used for [TextInputControl] objects (for example: [javafx.scene.control.TextField])
 * to avoid whitespaces.
 */
class SpaceValidator : TextFormatter<TextFormatter.Change?>({ change: Change ->
    val text = change.text
    when {
        StringUtils.isEmpty(text) -> change.text = text.replace("\\s+".toRegex(), StringUtils.EMPTY)
    }
    
    change
})