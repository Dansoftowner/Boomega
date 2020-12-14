package com.dansoftware.libraryapp.gui.util

import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.adapter.ThrowableString
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import org.apache.commons.lang3.StringUtils
import java.io.BufferedInputStream
import kotlin.reflect.KClass


fun MenuItem.action(onAction: EventHandler<ActionEvent>): MenuItem = this.also { this.onAction = onAction }

fun Menu.menuItem(item: MenuItem): Menu = this.also { this.items.add(item) }

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

    private fun createButtonType(key: String, buttonData: ButtonBar.ButtonData) =
        ButtonType(I18N.getButtonTypeValues().getString(key), buttonData)
}

class ExceptionDisplayPane(exception: Exception?) : TitledPane() {
    init {
        content = TextArea(ThrowableString(exception).toString())
        isAnimated = true
    }
}