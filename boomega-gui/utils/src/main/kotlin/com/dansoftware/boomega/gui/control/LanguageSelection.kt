/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:JvmName("LanguageSelections")

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.util.surrounding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.skin.TextFieldSkin
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import java.util.*
import java.util.function.Consumer

/**
 * Control for [TextField] that allows selecting languages.
 *
 * @author Daniel Gyorffy
 */
class TextFieldLanguageSelectorControl(context: Context, textField: TextField) : StackPane() {
    init {
        setAlignment(this, Pos.CENTER_RIGHT)
        children.add(buildGraphic(context, textField))
    }

    private fun buildGraphic(context: Context, textField: TextField): Node =
        icon("view-list-icon").also { icon ->
            icon.cursor = Cursor.HAND
            setAlignment(icon, Pos.CENTER_RIGHT)
            icon.setOnMouseClicked { event: MouseEvent ->
                if (event.button == MouseButton.PRIMARY) {
                    LanguageSelection(
                        context,
                        textField.text?.let { Locale.forLanguageTag(it) }
                    ) { locale: Locale -> textField.text = locale.language }.show()
                }
            }
            icon.styleClass.add("langSelectorControl")
        }

    companion object {
        /**
         * Adds a [TextFieldLanguageSelectorControl] to a [TextField]
         */
        @JvmStatic
        fun applyOnTextField(context: Context, textField: TextField) {
            textField.apply {
                skinProperty().addListener(object : ChangeListener<Skin<*>> {
                    override fun changed(
                        observable: ObservableValue<out Skin<*>>,
                        oldValue: Skin<*>?,
                        newSkin: Skin<*>?
                    ) {
                        newSkin?.let { it as TextFieldSkin }?.children?.add(
                            TextFieldLanguageSelectorControl(
                                context,
                                textField
                            )
                        )
                        observable.removeListener(this)
                    }
                })
                textProperty().addListener { _, _, newText ->
                    tooltip = newText?.let { Locale.forLanguageTag(it) }?.displayLanguage?.let(::Tooltip)
                }
            }
        }
    }
}

/**
 * A Utility UI Control that allows the user to select a language in the form of [Locale].
 *
 * @author Daniel Gyorffy
 */
class LanguageSelection(private val context: Context, initialSelected: Locale?, onSelection: Consumer<Locale>) :
    StackPane(), Callback<ListView<Locale>, ListCell<Locale>> {

    init {
        isPickOnBounds = false
        children.add(Group(ListView<Locale>().also { listView ->
            listView.items.addAll(listLocales())
            listView.cellFactory = this
            listView.setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    listView.selectionModel.selectedItem?.let { locale -> onSelection.accept(locale) }
                    context.hideOverlay(this)
                }
            }
            listView.items.firstOrNull { it.language == initialSelected?.language }?.also {
                listView.selectionModel.select(it)
                listView.scrollTo(it)
            }
        }))
    }

    private fun listLocales(): List<Locale> =
        LinkedList<Locale>().also { locales ->
            val set = HashSet<String>()
            Locale.getAvailableLocales().forEach {
                if (set.add(it.language)) {
                    locales.add(it)
                }
            }
            val basicComparator = String.CASE_INSENSITIVE_ORDER
            locales.sortWith { o1, o2 -> basicComparator.compare(o1.displayLanguage, o2.displayLanguage) }
        }


    fun show() = context.showOverlay(this)

    override fun call(param: ListView<Locale>): ListCell<Locale> =
        object : ListCell<Locale>() {
            override fun updateItem(item: Locale?, empty: Boolean) {
                super.updateItem(item, empty)
                this.text = when {
                    empty -> {
                        null
                    }
                    else -> {
                        item?.let {
                            "${it.displayLanguage} ${
                                when {
                                    it.language?.isNotBlank() ?: false ->
                                        it.language.surrounding("(", ")")
                                    else -> ""
                                }
                            }"
                        } ?: "-"
                    }
                }
            }
        }
}