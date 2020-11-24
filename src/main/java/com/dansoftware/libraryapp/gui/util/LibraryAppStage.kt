package com.dansoftware.libraryapp.gui.util

import com.dansoftware.libraryapp.locale.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import org.apache.commons.lang3.StringUtils
import java.io.BufferedInputStream

/**
 * A [LibraryAppStage] is a [Stage] implementation that
 * supports internationalized titles and automatically adds the libraryapp icon-bundle.
 *
 * @author Daniel Gyorffy
 */
abstract class LibraryAppStage : Stage {

    /**
     * For defining the resource-locations for window-icons
     */
    companion object {
        /**
         * The 16px libraryapp icon's path.
         * The icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
         *
         *
         * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
         */
        private const val LOGO_16 = "/com/dansoftware/libraryapp/image/logo/bookshelf_16.png"

        /**
         * The 32px libraryapp icon's path.
         * The icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
         *
         *
         * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
         */
        private const val LOGO_32 = "/com/dansoftware/libraryapp/image/logo/bookshelf_32.png"

        /**
         * The 128px libraryapp icon's path.
         * The icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
         *
         *
         * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
         */
        private const val LOGO_128 = "/com/dansoftware/libraryapp/image/logo/bookshelf_128.png"

        /**
         * The 256px libraryapp icon's path.
         * The icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
         *
         *
         * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
         */
        private const val LOGO_256 = "/com/dansoftware/libraryapp/image/logo/bookshelf_256.png"

        /**
         * The 512px libraryapp icon's path.
         * The icon made by [Freepik](https://www.flaticon.com/authors/freepik) from [ www.flaticon.com](https://www.flaticon.com/)
         *
         *
         * [Go to website](https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12)
         */
        private const val LOGO_512 = "/com/dansoftware/libraryapp/image/logo/bookshelf_512.png"
    }

    init {
        this.icons.addAll(
                loadImage(LOGO_16),
                loadImage(LOGO_32),
                loadImage(LOGO_128),
                loadImage(LOGO_256),
                loadImage(LOGO_512)
        )
    }

    private fun loadImage(url: String): Image {
        BufferedInputStream(this.javaClass.getResourceAsStream(url)).use {
            return Image(it)
        }
    }

    /**
     * Creates a normal LibraryAppStage.
     */
    protected constructor() : super()

    /**
     * Creates a LibraryAppStage and sets the title of it.
     *
     * @param i18n the resource bundle key for the title
     */
    protected constructor(i18n: String) : this() {
        title = I18N.getWindowTitles().getString(i18n)
    }

    /**
     * Creates a LibraryAppStage and sets the title and the content of it.
     *
     * @param i18n the resource bundle key for the title
     * @param content the graphic content
     */
    protected constructor(i18n: String, content: Parent) : this(i18n) {
        scene = Scene(content)
    }

    protected constructor(i18n: String, separator: String, changingString: ObservableStringValue, content: Parent) {
        scene = Scene(content)
        titleProperty().bind(buildTitleProperty(i18n, separator, changingString))
    }

    private fun buildTitleProperty(i18n: String, separator: String, changingString: ObservableStringValue): ObservableValue<String> {

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

            override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) = setValue()
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