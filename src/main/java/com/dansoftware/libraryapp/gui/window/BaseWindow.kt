package com.dansoftware.libraryapp.gui.window

import com.dansoftware.libraryapp.gui.context.ContextTransformable
import com.dansoftware.libraryapp.gui.util.loadImageResource
import com.dansoftware.libraryapp.gui.util.typeEquals
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.main.ApplicationRestart
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.apache.commons.lang3.StringUtils

/**
 * Represents the key-combination that is used for
 * restarting the application
 */
private val restartKeyCombination = KeyCodeCombination(
    KeyCode.R,
    KeyCombination.ModifierValue.DOWN,
    KeyCombination.ModifierValue.DOWN,
    KeyCombination.ModifierValue.ANY,
    KeyCombination.ModifierValue.ANY,
    KeyCombination.ModifierValue.ANY
)


/**
 * A [BaseWindow] is a [Stage] implementation that
 * supports internationalized titles and automatically adds the libraryapp icon-bundle.
 *
 * Also, it provides support for dialogs on restart key combination, and on window close event.
 *
 * @param C the type of the content that is shown in the Window's scene
 * @author Daniel Gyorffy
 */
abstract class BaseWindow<C> : Stage
        where C : Parent, C : ContextTransformable {

    private lateinit var content: C
    protected var exitDialog: Boolean = false

    init {
        setupIconPack()
        buildRestartKeyCombination()
        buildExitDialogEvent()
    }

    /**
     * Creates a normal LibraryAppStage.
     */
    private constructor() : super()

    /**
     * Creates a LibraryAppStage and sets the title of it.
     *
     * @param i18n the resource bundle key for the title
     */
    private constructor(i18n: String) : this() {
        title = I18N.getWindowTitles().getString(i18n)
    }

    /**
     * Creates a LibraryAppStage and sets the title and the content of it.
     *
     * @param i18n the resource bundle key for the title
     * @param content the graphic content
     */
    protected constructor(i18n: String, content: C) : this(i18n) {
        this.content = content
        this.scene = Scene(content)
    }

    protected constructor(i18n: String, separator: String, additionalTitleValue: String, content: C) {
        this.title = "${I18N.getWindowTitles().getString(i18n)} $separator $additionalTitleValue"
        this.content = content
        this.scene = Scene(content)
    }

    protected constructor(i18n: String, separator: String, changingString: ObservableStringValue, content: C) {
        this.content = content
        this.scene = Scene(content)
        this.titleProperty().bind(TitleProperty(i18n, separator, changingString))
    }

    private fun setupIconPack() {
        this.icons.addAll(
            BaseWindow::class.loadImageResource(LOGO_16),
            BaseWindow::class.loadImageResource(LOGO_32),
            BaseWindow::class.loadImageResource(LOGO_128),
            BaseWindow::class.loadImageResource(LOGO_256),
            BaseWindow::class.loadImageResource(LOGO_512)
        )
    }

    private fun buildExitDialogEvent() {
        this.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, WindowCloseRequestHandler())
    }

    private fun buildRestartKeyCombination() {
        sceneProperty().addListener(object : ChangeListener<Scene> {
            override fun changed(observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) {
                newValue?.onKeyPressed = RestartKeyCombinationPressedHandler().also {
                    observable.removeListener(this)
                }
            }
        })
    }

    /**
     * Sets the full screen key combination.
     */
    protected fun setFullScreenKeyCombination(value: KeyCombination) {
        this.addEventHandler(KeyEvent.KEY_RELEASED) { event: KeyEvent ->
            if (value.match(event)) {
                isFullScreen = isFullScreen.not()
            }
        }
    }

    /**
     * A [TitleProperty] is a utility for creating dynamically changing window title
     */
    private class TitleProperty(i18n: String, separator: String, changingString: ObservableStringValue) :
        SimpleStringProperty() {

        init {
            val baseTitle = SimpleStringProperty(I18N.getWindowTitles().getString(i18n))
            val separatorAndChangingObservable = buildSeparatorAndChangingObservable(separator, changingString)
            this.bind(baseTitle.concat(separatorAndChangingObservable))
        }

        private fun buildSeparatorAndChangingObservable(
            separator: String,
            changingString: ObservableStringValue
        ): ObservableStringValue =
            object : SimpleStringProperty(), ChangeListener<String> {
                init {
                    copyValue(separator, changingString.value)
                    changingString.addListener(this)
                }

                private fun copyValue(separator: String, newValue: String) {
                    when (newValue) {
                        "null" -> this.set(StringUtils.EMPTY)
                        else -> this.set(separator.plus(newValue))
                    }
                }

                override fun changed(observable: ObservableValue<out String>, oldValue: String, newValue: String) =
                    copyValue(separator, newValue)
            }
    }

    /**
     * Key event handler for detecting the restart key combination and showing a restart dialog for the user.
     */
    private inner class RestartKeyCombinationPressedHandler : EventHandler<KeyEvent> {
        private var dialogShowing: Boolean = false

        override fun handle(keyEvent: KeyEvent) {
            if (dialogShowing.not() && restartKeyCombination.match(keyEvent)) {
                dialogShowing = true
                this@BaseWindow.content.context.showConfirmationDialog(
                    I18N.getGeneralValue("app.restart.dialog.title"),
                    I18N.getGeneralValue("app.restart.dialog.msg")
                ) {
                    when {
                        it.typeEquals(ButtonType.YES) -> ApplicationRestart().restartApp()
                    }
                    dialogShowing = false
                }
            }
        }
    }

    private inner class WindowCloseRequestHandler : EventHandler<WindowEvent> {

        private var dialogShowing: Boolean = false

        override fun handle(event: WindowEvent) {
            if (this@BaseWindow.exitDialog) {
                when {
                    dialogShowing.not() -> {
                        dialogShowing = true
                        val buttonType = this@BaseWindow.content.context.showConfirmationDialogAndWait(
                            I18N.getGeneralValue("window.close.dialog.title"),
                            I18N.getGeneralValue("window.close.dialog.msg")
                        )
                        dialogShowing = false
                        if (buttonType.typeEquals(ButtonType.NO)) {
                            event.consume()
                        }
                    }
                    else -> event.consume()
                }
            }
        }
    }

    /**
     * For defining the resource-locations for window-icons
     *
     * The icons made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
     * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
     */
    private companion object Icon {
        /**
         * The 16px libraryapp icon's path.
         */
        private const val LOGO_16 = "/com/dansoftware/libraryapp/image/logo/bookshelf_16.png"

        /**
         * The 32px libraryapp icon's path.
         */
        private const val LOGO_32 = "/com/dansoftware/libraryapp/image/logo/bookshelf_32.png"

        /**
         * The 128px libraryapp icon's path.
         */
        private const val LOGO_128 = "/com/dansoftware/libraryapp/image/logo/bookshelf_128.png"

        /**
         * The 256px libraryapp icon's path.
         */
        private const val LOGO_256 = "/com/dansoftware/libraryapp/image/logo/bookshelf_256.png"

        /**
         * The 512px libraryapp icon's path.
         */
        private const val LOGO_512 = "/com/dansoftware/libraryapp/image/logo/bookshelf_512.png"
    }
}