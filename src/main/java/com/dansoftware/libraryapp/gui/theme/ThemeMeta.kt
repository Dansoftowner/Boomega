package com.dansoftware.libraryapp.gui.theme

import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.function.Supplier

/**
 * Used for storing some meta-data about a [Theme] implementation
 */
data class ThemeMeta<T : Theme>(val themeClass: Class<T>, val displayNameSupplier: Supplier<String>, val designer: ThemeDesigner) {
    init {
        Objects.requireNonNull(themeClass)
        Objects.requireNonNull(designer)
    }
}

/**
 * Represents the person or company who created a particular [Theme] implementation
 */
open class ThemeDesigner(val firstName: String, val lastName: String = StringUtils.EMPTY)