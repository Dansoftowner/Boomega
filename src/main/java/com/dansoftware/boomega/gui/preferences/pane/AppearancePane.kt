package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.ThemeMeta
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.ReflectionUtils
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.util.StringConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppearancePane(preferences: Preferences) : PreferencesPane(preferences) {
    override val title = I18N.getValue("preferences.tab.appearance")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.FORMAT_PAINT)

    override fun buildContent(): Content = object : Content() {
        init {
            initEntries()
        }

        private fun initEntries() {
            buildThemeSelect()
            buildWindowOpacitySlider()
        }

        private fun buildWindowOpacitySlider() {
            Slider(20.0, 100.0, BaseWindow.globalOpacity.value * 100).apply {
                valueProperty().addListener { _, _, value ->
                    value.toDouble().div(100)
                        .let(BaseWindow.globalOpacity::set)
                }
                valueChangingProperty().addListener { _, _, changing ->
                    changing.takeIf { it.not() }?.let {
                        logger.debug("Global opacity saved to configurations")
                        preferences.editor().put(BaseWindow.GLOBAL_OPACITY_CONFIG_KEY, value.div(100))
                    }
                }
            }.also {
                addEntry(
                    I18N.getValue("preferences.appearance.window_opacity"),
                    I18N.getValue("preferences.appearance.window_opacity.desc"),
                    it
                )
            }
        }

        private fun buildThemeSelect() {
            ChoiceBox<ThemeMeta<*>>().apply {

                this.converter = object : StringConverter<ThemeMeta<*>?>() {
                    override fun toString(themeMeta: ThemeMeta<*>?): String {
                        return themeMeta?.displayNameSupplier?.get() ?: ""
                    }

                    override fun fromString(string: String?): ThemeMeta<*>? {
                        //TODO("Not yet implemented")
                        return null
                    }
                }

                Theme.getAvailableThemesData().forEach {
                    if (Theme.getDefault().javaClass == it.themeClass)
                        selectionModel.select(it)
                    items.add(it)
                }

                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    try {
                        val themeObject = ReflectionUtils.constructObject(it.themeClass)
                        logger.debug("The theme object: {}", themeObject)
                        Theme.setDefault(themeObject)
                        preferences.editor().put(Preferences.Key.THEME, themeObject)
                    } catch (e: Exception) {
                        logger.error("Couldn't set the theme", e)
                        // TODO: error dialog
                    }
                }
            }.let {
                addEntry(
                    I18N.getValue("preferences.appearance.theme"),
                    I18N.getValue("preferences.appearance.theme.desc"),
                    it
                )
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppearancePane::class.java)
    }
}