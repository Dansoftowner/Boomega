package com.dansoftware.libraryapp.gui.window

import com.dansoftware.libraryapp.gui.context.ContextTransformable
import com.dansoftware.libraryapp.gui.util.loadImageResource
import com.dansoftware.libraryapp.gui.util.typeEquals
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.main.ApplicationRestart
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
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
 * A [LibraryAppStage] is a [Stage] implementation that
 * supports internationalized titles and automatically adds the libraryapp icon-bundle.
 *
 * @param C the type of the content that is shown in the scene
 * @author Daniel Gyorffy
 */
abstract class LibraryAppStage<C> : Stage where C : Parent, C : ContextTransformable {

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

    private lateinit var content: C
    private var exitDialogNeeded: Boolean = false

    init {
        this.icons.addAll(
            LibraryAppStage::class.loadImageResource(LOGO_16),
            LibraryAppStage::class.loadImageResource(LOGO_32),
            LibraryAppStage::class.loadImageResource(LOGO_128),
            LibraryAppStage::class.loadImageResource(LOGO_256),
            LibraryAppStage::class.loadImageResource(LOGO_512)
        )
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

    protected constructor(i18n: String, separator: String, changingString: ObservableStringValue, content: C) {
        this.content = content
        this.scene = Scene(content)
        this.titleProperty().bind(buildTitleProperty(i18n, separator, changingString))
    }

    fun setExitDialog(value: Boolean) {
        this.exitDialogNeeded = value
    }

    private fun buildExitDialogEvent() {
        this.setOnCloseRequest { event ->
            when {
                exitDialogNeeded ->
                    content.context.showConfirmationDialog(
                        I18N.getGeneralWord("window.close.dialog.title"),
                        I18N.getGeneralWord("window.close.dialog.msg")
                    ) {
                        when {
                            it.typeEquals(ButtonType.YES) -> Platform.exit()
                        }
                    }
            }
        }
    }

    private fun buildRestartKeyCombination() {

        sceneProperty().addListener(object : ChangeListener<Scene> {
            override fun changed(observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) {
                if (newValue != null) {
                    newValue.setOnKeyPressed { keyEvent ->
                        if (restartKeyCombination.match(keyEvent)) {
                            content.context.showConfirmationDialog(
                                I18N.getGeneralWord("app.restart.dialog.title"),
                                I18N.getGeneralWord("app.restart.dialog.msg")
                            ) {
                                when {
                                    it.typeEquals(ButtonType.YES) -> ApplicationRestart().restartApp()
                                }
                            }

                        }
                    }
                    observable.removeListener(this)
                }
            }
        })
    }

    private fun buildTitleProperty(
        i18n: String,
        separator: String,
        changingString: ObservableStringValue
    ): ObservableValue<String> {

        class ChangingProperty : SimpleStringProperty(), ChangeListener<String> {
            private val separatorAndValueProperty = SimpleStringProperty(separator).concat(changingString)

            init {
                setValue()
                changingString.addListener(this)
            }

            private fun setValue() {
                when {
                    changingString.get() == "null" -> {
                        this.unbind()
                        this.set(StringUtils.EMPTY)
                    }
                    else -> {
                        this.bind(separatorAndValueProperty)
                    }
                }
            }

            override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) =
                setValue()
        }

        return SimpleStringProperty(I18N.getWindowTitles().getString(i18n)).concat(ChangingProperty())
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
}