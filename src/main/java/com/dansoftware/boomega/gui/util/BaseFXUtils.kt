/**
 * Provides utilities in the subject of JavaFX
 *
 * @author Daniel Gyorffy
 */
@file:JvmName("BaseFXUtils")

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.util.equalsIgnoreCase
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.stage.Window
import java.io.BufferedInputStream
import java.util.function.Consumer
import kotlin.reflect.KClass

fun <T> ObservableList<T>.copy(): ObservableList<T> =
    FXCollections.observableArrayList(java.util.List.copyOf(this))

fun runOnUiThread(action: Runnable) {
    when {
        Platform.isFxApplicationThread() -> action.run()
        else -> Platform.runLater(action)
    }
}

fun <T> ComboBox<T>.refresh() {
    val items: ObservableList<T> = this.items
    val selected: T = this.selectionModel.selectedItem
    this.items = null
    this.items = items
    this.selectionModel.select(selected)
}

fun findSelectedRadioItem(items: List<MenuItem>): MenuItem? =
    items.filterIsInstance<RadioMenuItem>()
        .find { it.isSelected }

fun <T> constantObservable(value: () -> T): ObservableValue<T> =
    object : ObservableValueBase<T>() {
        override fun getValue(): T = value()
    }

fun loadImage(resource: String, onImageReady: Consumer<Image>) {
    val image = Image(resource, true)
    image.progressProperty().addListener(object : ChangeListener<Number> {
        override fun changed(observable: ObservableValue<out Number>, oldValue: Number, newValue: Number) {
            if (newValue == 1.0 && image.isError.not()) {
                onImageReady.accept(image)
                observable.removeListener(this)
            }
        }
    })
}

fun Node.onWindowPresent(action: Consumer<Window>) {
    this.scene?.window?.also { action.accept(it) }
        ?: this.sceneProperty().addListener(object : ChangeListener<Scene> {
            override fun changed(observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) {
                if (newValue != null) {
                    scene.windowProperty().addListener(object : ChangeListener<Window> {
                        override fun changed(
                            observable: ObservableValue<out Window>,
                            oldValue: Window?,
                            newValue: Window?
                        ) {
                            if (newValue != null) {
                                action.accept(newValue)
                                observable.removeListener(this)
                            }
                        }

                    })
                    observable.removeListener(this)
                }
            }
        })
}

/**
 * Sets the action of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.action(onAction: EventHandler<ActionEvent>): M = this.also { this.onAction = onAction }

/**
 * Sets the key combination of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.keyCombination(combination: KeyCombination): M = this.also { it.accelerator = combination }

/**
 * Binds the key combination property of the [MenuItem] to the given property and then returns the object itself
 */
fun <M : MenuItem, T : KeyCombination> M.keyCombination(combination: ObjectProperty<T>) =
    this.apply { acceleratorProperty().bind(combination) }

/**
 * Sets the icon of the [MenuItem] and then returns the object itself
 */
fun <M : MenuItem> M.graphic(icon: MaterialDesignIcon): M = this.also { it.graphic = MaterialDesignIconView(icon) }

/**
 * Adds a sub menu item into the [Menu] and then returns the object itself
 */
fun <M : Menu> M.menuItem(item: MenuItem): M = this.also { items.add(item) }

/**
 * Adds a [SeparatorMenuItem] into the [Menu] and then returns the object itself
 */
fun <M : Menu> M.separator(): M = this.also { this.items.add(SeparatorMenuItem()) }

/**
 * Utility function that converts a [KeyCodeCombination] into a [KeyEvent] object,
 * simulating that the particular key-combination is pressed by the user
 */
fun KeyCodeCombination.asKeyEvent(): KeyEvent =
    KeyEvent(
        KeyEvent.KEY_PRESSED,
        this.code.toString(),
        this.displayText,
        this.code,
        this.shift == KeyCombination.ModifierValue.DOWN,
        this.control == KeyCombination.ModifierValue.DOWN,
        this.alt == KeyCombination.ModifierValue.DOWN,
        this.meta == KeyCombination.ModifierValue.DOWN
    )

fun KeyEvent.asKeyCombination(): KeyCombination? =
    mutableListOf<KeyCombination.Modifier>().also { modifiers ->
        this.isControlDown.takeIf { it }?.let { modifiers.add(KeyCombination.CONTROL_DOWN) }
        this.isAltDown.takeIf { it }?.let { modifiers.add(KeyCombination.ALT_DOWN) }
        this.isShiftDown.takeIf { it }?.let { modifiers.add(KeyCombination.SHIFT_DOWN) }
        this.isMetaDown.takeIf { it }?.let { modifiers.add(KeyCombination.META_DOWN) }
        this.isShortcutDown.takeIf { it }?.let { modifiers.add(KeyCombination.SHORTCUT_DOWN) }
    }.let {
        this.code?.let { _ ->
            try {
                when {
                    it.isEmpty()
                        .and(this.code.isFunctionKey.not())
                        .and(this.code.isNavigationKey.not())
                        .and(this.code != KeyCode.DELETE)
                        .and(this.code != KeyCode.INSERT) -> throw RuntimeException()
                    else -> KeyCodeCombination(this.code, *it.toTypedArray())
                }
            } catch (e: RuntimeException) {
                null
            }
        }
    }

fun KeyEvent.isOnlyCode(): Boolean {
    return listOf(
        this.isControlDown.takeIf { it },
        this.isAltDown.takeIf { it },
        this.isShiftDown.takeIf { it },
        this.isMetaDown.takeIf { it },
        this.isShortcutDown.takeIf { it }
    ).count { it !== null } == 0
}

fun KeyEvent.isUndefined(): Boolean =
    this.code.name.equalsIgnoreCase("undefined")


/**
 * Determines that a ButtonType's button data is the same.
 */
fun ButtonType.typeEquals(other: ButtonType) = this.buttonData == other.buttonData

/**
 * Loads a resource into a javaFX [Image]
 */
fun KClass<*>.loadImageResource(resource: String): Image {
    BufferedInputStream(this.java.getResourceAsStream(resource)).use {
        return Image(it)
    }
}