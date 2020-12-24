package com.dansoftware.libraryapp.gui.util

import com.dansoftware.libraryapp.gui.context.Context
import javafx.scene.Group
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import javafx.util.Callback
import java.util.*
import java.util.function.Consumer

/**
 * A Utility UI Control that allows the user to select a language in the form of [Locale].
 *
 * @author Daniel Gyorffy
 */
class LanguageSelection(private val context: Context, onSelection: Consumer<Locale>) : StackPane(),
    Callback<ListView<Locale>, ListCell<Locale>> {

    init {
        isPickOnBounds = false
        children.add(Group(ListView<Locale>().also {
            it.items.addAll(Locale.getAvailableLocales())
            it.cellFactory = this
            it.setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    it.selectionModel.selectedItem?.let { locale -> onSelection.accept(locale) }
                    context.hideOverlay(this)
                }
            }
        }))
    }

    fun show() = context.showOverlay(this)

    override fun call(param: ListView<Locale>): ListCell<Locale> = object : ListCell<Locale>() {
        override fun updateItem(item: Locale?, empty: Boolean) {
            super.updateItem(item, empty)
            text = when {
                empty -> {
                    null
                }
                else -> {
                    item?.displayLanguage ?: "-"
                }
            }
        }
    }
}