package com.dansoftware.boomega.gui.firsttime.segment.theme

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.theme.DarkTheme
import com.dansoftware.boomega.gui.theme.LightTheme
import com.dansoftware.boomega.gui.theme.OsSynchronizedTheme
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.i18n.I18N
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class ThemeSegmentView(private val preferences: Preferences) : StackPane() {

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(Group(buildHBox()))
    }

    private fun buildHBox() = HBox(10.0).apply {
        buildRadioGroup().also { group ->
            buildToggle(
                "segment.theme.light",
                "/com/dansoftware/boomega/image/firsttime/ThemeLight.png",
                group,
                LightTheme::class
            ) { LightTheme() }.let(children::add)

            buildToggle(
                "segment.theme.dark",
                "/com/dansoftware/boomega/image/firsttime/ThemeDark.png",
                group,
                DarkTheme::class
            ) { DarkTheme() }.let(children::add)

            buildToggle(
                "segment.theme.sync",
                "/com/dansoftware/boomega/image/firsttime/ThemeSynchronized.png",
                group,
                OsSynchronizedTheme::class
            ) { OsSynchronizedTheme() }.let(children::add)
        }
    }

    private fun <T : Theme> buildToggle(
        i18n: String,
        thumbnailPath: String,
        group: ToggleGroup,
        themeClass: KClass<T>,
        themeFactory: () -> T
    ) = ThemeToggle(
        I18N.getValue(i18n),
        ImageView(thumbnailPath).apply {
            fitHeight = 245.0
            fitWidth = 233.0
        },
        themeClass.java,
        themeFactory
    ).apply { toggleGroup = group }

    private fun buildRadioGroup() = ToggleGroup().apply {
        selectedToggleProperty().addListener { _, _, newItem ->
            newItem?.let {
                if (it is ThemeToggle<*>) {
                    val theme =
                        if (Theme.getDefault().javaClass == it.themeClass) Theme.getDefault() else it.themeFactory()
                    preferences.editor().put(PreferenceKey.THEME, theme).tryCommit()
                    Theme.setDefault(theme)
                    logger.debug("Theme selected: {}", Theme.getDefault().javaClass.name)
                }
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ThemeSegmentView::class.java)
    }

    private class ThemeToggle<T : Theme>(
        text: String,
        thumbnail: Node,
        val themeClass: Class<T>,
        val themeFactory: () -> T,
    ) : RadioButton() {
        init {
            this.text = text
            this.contentDisplay = ContentDisplay.BOTTOM
            this.graphic = thumbnail
            this.isSelected = Theme.getDefault().javaClass == themeClass
            this.padding = Insets(10.0)
        }
    }
}