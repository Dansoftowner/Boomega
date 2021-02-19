package com.dansoftware.boomega.gui.theme

/**
 * Used for creating the default [Theme] object
 */
object DefaultThemeFactory {
    fun get(): Theme = OsSynchronizedTheme()
}