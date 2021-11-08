package com.dansoftware.boomega.gui.firsttime.segment.lang

import com.dansoftware.boomega.config.LOCALE
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.i18n.I18N
import javafx.geometry.Insets
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import java.util.*

class LanguageSegmentView(private val preferences: Preferences) : StackPane() {

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.add(buildListView())
    }

    private fun buildListView() = ListView<LanguageEntry>().apply {
        selectionModel.selectedItemProperty().addListener { _, oldItem, newItem ->
            newItem?.let {
                preferences.editor().put(LOCALE, it.locale)
                Locale.setDefault(it.locale)
            } ?: selectionModel.select(oldItem) //we don't allow the user to choose no items
        }
        fillListView()
    }

    private fun ListView<LanguageEntry>.fillListView() {
        val availableLocales = ArrayList(I18N.getAvailableLocales())
        val defaultLocaleIndex = availableLocales.indexOf(I18N.defaultLocale())
        items.addAll(availableLocales.map(::LanguageEntry))
        selectionModel.select(defaultLocaleIndex)
        scrollTo(defaultLocaleIndex)
    }


    /**
     * Represents an item in the ListView
     */
    private class LanguageEntry(val locale: Locale) {
        override fun toString(): String {
            return locale.displayName
        }
    }
}