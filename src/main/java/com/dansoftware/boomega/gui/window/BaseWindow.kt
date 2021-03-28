package com.dansoftware.boomega.gui.window

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.Themeable
import com.dansoftware.boomega.gui.util.loadImageResource
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.util.os.OsInfo
import de.jangassen.MenuToolkit
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.MenuBar
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A [BaseWindow] is a [Stage] implementation that
 * supports internationalized titles and automatically adds the libraryapp icon-bundle.
 *
 * Also, it provides support for dialogs on restart key combination, and on window close event.
 *
 * @param C the type of the content that is shown in the Window's scene
 * @author Daniel Gyorffy
 */
abstract class BaseWindow : Stage, Themeable {

    lateinit var getContext: () -> Context?
    protected var exitDialog: Boolean = false

    init {
        setupIconPack()
        buildRestartKeyCombination()
        buildExitDialogEvent()
        buildFullScreenExitHint()
        addEventHandler(WindowEvent.WINDOW_SHOWING) { Theme.registerThemeable(this) }
        opacityProperty().bind(globalOpacity)
    }

    /**
     * Creates a normal LibraryAppStage.
     */
    constructor() : super()

    /**
     * Creates a LibraryAppStage and sets the title of it.
     *
     * @param i18n the resource bundle key for the title
     */
    private constructor(i18n: String) : this() {
        title = I18N.getValues().getString(i18n)
    }

    /**
     * Creates a LibraryAppStage and sets the title and the content of it.
     *
     * @param i18n the resource bundle key for the title
     * @param content the graphic content
     */
    protected constructor(i18n: String, content: Parent, getContext: () -> Context?) : this(i18n) {
        this.scene = Scene(content)
        this.getContext = getContext
    }


    protected constructor(
        baseTitle: String,
        separator: String,
        additionalTitleValue: String,
        content: Parent,
        getContext: () -> Context?
    ) {
        this.title = "$baseTitle $separator $additionalTitleValue"
        this.scene = Scene(content)
        this.getContext = getContext
    }

    protected constructor(
        title: String,
        menuBar: MenuBar,
        content: Parent,
        getContext: () -> Context?
    ) {
        this.title = title
        this.scene = Scene(buildMenuBarContent(content, menuBar))
        this.getContext = getContext
    }

    protected constructor(
        i18n: String,
        separator: String,
        changingString: ObservableStringValue,
        content: Parent,
        getContext: () -> Context?
    ) {
        this.scene = Scene(content)
        this.titleProperty().bind(TitleProperty(i18n, separator, changingString))
        this.getContext = getContext
    }

    override fun handleThemeApply(oldTheme: Theme, newTheme: Theme) {
        scene?.root?.let {
            oldTheme.applyBack(it)
            newTheme.apply(it)
        }
    }

    fun makeFocused() {
        this.isIconified = false
        this.toFront()
    }

    private fun buildMenuBarContent(content: Parent, menuBar: MenuBar): Parent =
        when {
            OsInfo.isMac() -> content.also {
                logger.debug("MacOS detected: building native menu bar...")
                MenuToolkit.toolkit().setMenuBar(this, menuBar)
            }
            else -> {
                logger.debug("MacOS is not detected: building JavaFX based menu-bar...")
                BorderPane(content).apply { top = menuBar }
            }
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

    private fun buildFullScreenExitHint() {
        fullScreenExitHint = I18N.getValue("window.fullscreen.hint")
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
            val baseTitle = SimpleStringProperty(I18N.getValues().getString(i18n))
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
            if (dialogShowing.not() && KeyBindings.restartApplicationKeyBinding.match(keyEvent)) {
                dialogShowing = true
                this@BaseWindow.getContext()?.showConfirmationDialog(
                    I18N.getValue("app.restart.dialog.title"),
                    I18N.getValue("app.restart.dialog.msg")
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
            this@BaseWindow.getContext()?.also { context ->
                if (this@BaseWindow.exitDialog) {
                    this@BaseWindow.makeFocused()
                    when {
                        dialogShowing.not() -> {
                            dialogShowing = true
                            val buttonType = context.showConfirmationDialogAndWait(
                                I18N.getValue("window.close.dialog.title"),
                                I18N.getValue("window.close.dialog.msg")
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
    }

    /**
     * The icons made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
     * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
     */
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BaseWindow::class.java)

        @JvmField
        val GLOBAL_OPACITY_CONFIG_KEY = Preferences.Key("basewindow.global.opacity", Double::class.java) { 1.0 }
        @JvmField
        val globalOpacity: DoubleProperty = SimpleDoubleProperty(1.0)

        /**
         * The 16px libraryapp icon's path.
         */
        private const val LOGO_16 = "/com/dansoftware/boomega/image/logo/bookshelf_16.png"

        /**
         * The 32px libraryapp icon's path.
         */
        private const val LOGO_32 = "/com/dansoftware/boomega/image/logo/bookshelf_32.png"

        /**
         * The 128px libraryapp icon's path.
         */
        private const val LOGO_128 = "/com/dansoftware/boomega/image/logo/bookshelf_128.png"

        /**
         * The 256px libraryapp icon's path.
         */
        private const val LOGO_256 = "/com/dansoftware/boomega/image/logo/bookshelf_256.png"

        /**
         * The 512px libraryapp icon's path.
         */
        private const val LOGO_512 = "/com/dansoftware/boomega/image/logo/bookshelf_512.png"
    }
}