/**
 * Provides utilities in the subject of JavaFX
 *
 * @author Daniel Gyorffy
 */
@file:JvmName("BaseFXUtils")

package com.dansoftware.libraryapp.gui.util

import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.adapter.ThrowableString
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Window
import org.apache.commons.lang3.StringUtils
import org.controlsfx.control.Rating
import java.io.BufferedInputStream
import java.util.function.Consumer
import kotlin.reflect.KClass

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
    this.sceneProperty().addListener(object : ChangeListener<Scene> {
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
fun MenuItem.action(onAction: EventHandler<ActionEvent>): MenuItem = this.also { this.onAction = onAction }

/**
 * Sets the key combination of the [MenuItem] and then returns the object itself
 */
fun MenuItem.keyCombination(combination: KeyCombination): MenuItem = this.also { it.accelerator = combination }

/**
 * Sets the icon of the [MenuItem] and then returns the object itself
 */
fun MenuItem.graphic(icon: MaterialDesignIcon): MenuItem = this.also { it.graphic = MaterialDesignIconView(icon) }

/**
 * Adds a sub menu item into the [Menu] and then returns the object itself
 */
fun Menu.menuItem(item: MenuItem): Menu = this.also { this.items.add(item) }

/**
 * Adds a [SeparatorMenuItem] into the [Menu] and then returns the object itself
 */
fun Menu.separator(): Menu = this.also { this.items.add(SeparatorMenuItem()) }

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

class ReadOnlyRating(max: Int, value: Int) : Rating(max, value) {
    init {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED) { it.consume() }
    }
}

class SelectableLabel(text: String? = null) : TextField(text) {
    init {
        this.styleClass.clear()
        this.styleClass.add("selectable-label")
        this.cursor = Cursor.TEXT
        this.style = "-fx-background-color: transparent;-fx-padding: 0;"
        this.prefColumnCount = 15
        this.styleClass.add("label")
        this.isEditable = false
    }
}

/**
 * The [RadioToggleButton] is a [RadioButton] styled as a [ToggleButton]
 */
class RadioToggleButton(text: String? = null) : RadioButton(text) {
    init {
        styleClass.remove("radio-button")
        styleClass.add("toggle-button")
    }
}

open class ImagePlaceHolder(size: Double) : Text("\uF2E9") {
    init {
        this.font = Font.font("Material Design Icons", size)
        this.styleClass.add("glyph-icon")
    }
}

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

/**
 * Provides internationalized [ButtonType] constants for the app.
 *
 * @author Daniel Gyorffy
 */
object I18NButtonTypes {
    @JvmField
    val APPLY = createButtonType("Dialog.apply.button", ButtonBar.ButtonData.APPLY)

    @JvmField
    val OK = createButtonType("Dialog.ok.button", ButtonBar.ButtonData.OK_DONE)

    @JvmField
    val CANCEL = createButtonType("Dialog.cancel.button", ButtonBar.ButtonData.CANCEL_CLOSE)

    @JvmField
    val CLOSE = createButtonType("Dialog.close.button", ButtonBar.ButtonData.CANCEL_CLOSE)

    @JvmField
    val YES = createButtonType("Dialog.yes.button", ButtonBar.ButtonData.YES)

    @JvmField
    val NO = createButtonType("Dialog.no.button", ButtonBar.ButtonData.NO)

    @JvmField
    val FINISH = createButtonType("Dialog.finish.button", ButtonBar.ButtonData.FINISH)

    @JvmField
    val NEXT = createButtonType("Dialog.next.button", ButtonBar.ButtonData.NEXT_FORWARD)

    @JvmField
    val PREVIOUS = createButtonType("Dialog.previous.button", ButtonBar.ButtonData.BACK_PREVIOUS)

    @JvmField
    val RETRY = createButtonType("Dialog.retry.button", ButtonBar.ButtonData.YES)

    private fun createButtonType(key: String, buttonData: ButtonBar.ButtonData) =
        ButtonType(I18N.getButtonTypeValues().getString(key), buttonData)
}

class ExceptionDisplayPane(exception: Exception?) : TitledPane() {
    init {
        content = TextArea(ThrowableString(exception).toString())
        isAnimated = true
        isExpanded = false
    }
}