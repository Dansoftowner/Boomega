/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.window

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.os.OsInfo
import com.dansoftware.boomega.util.resImg
import de.jangassen.MenuToolkit
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.geometry.NodeOrientation
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.jetbrains.annotations.NonNls
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A [BaseWindow] is a [Stage] implementation that
 * supports internationalized titles and automatically adds the Boomega icon-bundle.
 *
 * Also, it provides support for dialogs on restart key combination, and on window close event.
 *
 * @param C the type of the content that is shown in the Window's scene
 * @author Daniel Gyorffy
 */
abstract class BaseWindow<C> : Stage, Theme.DefaultThemeListener where C : Parent, C : Context {

    protected var content: C? = null
        private set
    protected var exitDialog: Boolean = false

    init {
        setupIconPack()
        buildExitDialogEvent()
        buildFullScreenExitHint()
        buildRTLSupport()
        addEventHandler(WindowEvent.WINDOW_SHOWING) { Theme.registerListener(this) }
        opacityProperty().bind(globalOpacity)
    }

    constructor() : super()

    /**
     * Creates a BaseWindow with an initial title.
     *
     * @param title the title
     */
    private constructor(title: String?) : this() {
        this.title = title
    }

    /**
     * Creates a BaseWindow with an initial title and content.
     *
     * @param title the title
     * @param content the graphic content
     */
    protected constructor(title: String?, content: C) : this(title, null, content)

    /**
     * Creates a BaseWindow with an initial title, [MenuBar] on the top and content.
     */
    protected constructor(
        @NonNls title: String?,
        menuBar: MenuBar?,
        content: C,
    ) : this(title) {
        this.content = content
        this.scene = Scene(buildMenuBarContent(content, menuBar))
    }

    /**
     * Creates a BaseWindow with a title-property and content.
     *
     * @param title the string property to bind the window's title to
     * @param content the gui-content
     */
    protected constructor(
        title: StringProperty,
        menuBar: MenuBar?,
        content: C
    ) : this(null, menuBar, content) {
        titleProperty().bind(title)
    }

    override fun onDefaultThemeChanged(oldTheme: Theme?, newTheme: Theme) {
        scene?.root?.let {
            oldTheme?.deApply(it)
            newTheme.apply(it)
        }
    }

    fun makeFocused() {
        this.isIconified = false
        this.toFront()
    }

    private fun buildMenuBarContent(content: Parent, menuBar: MenuBar?): Parent {
        logger.debug("Building menu-bar content...")
        return when {
            menuBar == null -> {
                logger.debug("The given menu-bar is null")
                content
            }
            OsInfo.isMacOS -> content.also {
                logger.debug("MacOS detected: adding event handler...")
                addEventHandler(WindowEvent.WINDOW_SHOWN, object : EventHandler<WindowEvent> {
                    override fun handle(event: WindowEvent) {
                        logger.debug("Building native MacOS menu bar...")
                        MenuToolkit.toolkit().setMenuBar(this@BaseWindow, menuBar)
                        removeEventHandler(event.eventType, this)
                    }
                })
                focusedProperty().addListener { _, _, isFocused ->
                    if (isFocused) MenuToolkit.toolkit().setApplicationMenu(menuBar.menus[0])
                }
            }
            else -> {
                logger.debug("MacOS is not detected: building JavaFX based menu-bar...")
                BorderPane(content).apply { top = menuBar }
            }
        }
    }

    private fun setupIconPack() {
        this.icons.addAll(
            resImg(LOGO_16),
            resImg(LOGO_20),
            resImg(LOGO_24),
            resImg(LOGO_32),
            resImg(LOGO_40),
            resImg(LOGO_48),
            resImg(LOGO_64),
            resImg(LOGO_96),
            resImg(LOGO_128),
            resImg(LOGO_256),
            resImg(LOGO_512)
        )
    }

    private fun buildExitDialogEvent() {
        this.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, WindowCloseRequestHandler())
    }

    private fun buildFullScreenExitHint() {
        fullScreenExitHint = I18N.getValue("window.fullscreen.hint")
    }

    private fun buildRTLSupport() {
        sceneProperty().addListener { _, _, scene ->
            if (I18N.isRTL())
                scene.nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
        }
    }

    private inner class WindowCloseRequestHandler : EventHandler<WindowEvent> {

        private var dialogShowing: Boolean = false

        override fun handle(event: WindowEvent) {
            this@BaseWindow.content?.also { context ->
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
        val GLOBAL_OPACITY_CONFIG_KEY = PreferenceKey(
            "basewindow.global.opacity",
            Double::class.java
        ) { 1.0 }

        @JvmField
        val globalOpacity: DoubleProperty = SimpleDoubleProperty(1.0)

        /**
         * The 16px icon's path.
         */
        private const val LOGO_16 = "/com/dansoftware/boomega/image/logo/boomega_cg_16.png"

        /**
         * The 20px icon's path.
         */
        private const val LOGO_20 = "/com/dansoftware/boomega/image/logo/boomega_cg_20.png"

        /**
         * The 24px icon's path.
         */
        private const val LOGO_24 = "/com/dansoftware/boomega/image/logo/boomega_cg_24.png"

        /**
         * The 32px icon's path.
         */
        private const val LOGO_32 = "/com/dansoftware/boomega/image/logo/boomega_cg_32.png"

        /**
         * The 40px icon's path.
         */
        private const val LOGO_40 = "/com/dansoftware/boomega/image/logo/boomega_cg_40.png"

        /**
         * The 48px icon's path.
         */
        private const val LOGO_48 = "/com/dansoftware/boomega/image/logo/boomega_cg_48.png"

        /**
         * The 64px icon's path.
         */
        private const val LOGO_64 = "/com/dansoftware/boomega/image/logo/boomega_cg_64.png"

        /**
         * The 96px icon's path.
         */
        private const val LOGO_96 = "/com/dansoftware/boomega/image/logo/boomega_cg_96.png"

        /**
         * The 128px icon's path.
         */
        private const val LOGO_128 = "/com/dansoftware/boomega/image/logo/boomega_cg_128.png"

        /**
         * The 256px icon's path.
         */
        private const val LOGO_256 = "/com/dansoftware/boomega/image/logo/boomega_cg_256.png"

        /**
         * The 512px icon's path.
         */
        private const val LOGO_512 = "/com/dansoftware/boomega/image/logo/boomega_cg_512.png"
    }
}