@file:JvmName("LanguageSelections")

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.util.surrounding
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Skin
import javafx.scene.control.TextField
import javafx.scene.control.skin.TextFieldSkin
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashSet

/**
 * Adds a [TextFieldLanguageSelectorControl] to a [TextField]
 */
fun applyOnTextField(context: Context, textField: TextField) {
    textField.skinProperty().addListener(object : ChangeListener<Skin<*>> {
        override fun changed(observable: ObservableValue<out Skin<*>>, oldValue: Skin<*>?, newSkin: Skin<*>?) {
            newSkin?.let { it as TextFieldSkin }?.children?.add(TextFieldLanguageSelectorControl(context, textField))
            observable.removeListener(this)
        }
    })
}

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
        MaterialDesignIconView(MaterialDesignIcon.VIEW_LIST).also { icon ->
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
                                    StringUtils.isNotBlank(it.language) ->
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