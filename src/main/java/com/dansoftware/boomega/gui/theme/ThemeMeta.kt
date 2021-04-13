package com.dansoftware.boomega.gui.theme

import java.util.*
import java.util.function.Supplier

/**
 * Used for storing some meta-data about a [Theme] implementation
 */
data class ThemeMeta<T : Theme>(
    val themeClass: Class<T>,
    val displayNameSupplier: Supplier<String>,
    val designer: ThemeDesigner
) {
    val displayName: String
        get() = displayNameSupplier.get()

    init {
        Objects.requireNonNull(themeClass)
        Objects.requireNonNull(designer)
    }
}

/**
 * Represents the person or company who created a particular [Theme] implementation
 */
open class ThemeDesigner(val name: String) {
    @Deprecated("Use the one-arg constructor instead")
    constructor(firstname: String, lastname: String) : this("$firstname $lastname")
}