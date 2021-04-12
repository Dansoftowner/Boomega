package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.main.ApplicationRestart
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceBox
import javafx.util.StringConverter
import java.util.*

class LanguagePane(
    private val context: Context,
    preferences: Preferences
) : PreferencesPane(preferences) {
    
    override val title: String = I18N.getValue("preferences.tab.language")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.TRANSLATE)

    override fun buildContent(): Content = object : Content() {

        init {
            initEntries()
        }

        private fun initEntries() {
            buildLanguageSelect()
        }

        private fun buildLanguageSelect() {
            ChoiceBox<Locale>().apply {
                this.converter = object : StringConverter<Locale>() {
                    override fun toString(locale: Locale?): String = locale?.displayLanguage ?: ""
                    override fun fromString(string: String?): Locale = Locale.forLanguageTag(string)
                }

                I18N.getAvailableLocales().forEach {
                    this.items.add(it)
                    if (it == preferences.get(Preferences.Key.LOCALE))
                        this.selectionModel.select(it)
                }

                this.selectionModel.selectedItemProperty().addListener { _, _, locale ->
                    preferences.editor().put(Preferences.Key.LOCALE, locale)
                    context.showConfirmationDialog(
                        I18N.getValue("app.lang.restart.title"),
                        I18N.getValue("app.lang.restart.msg")
                    ) { btn ->
                        btn.takeIf { it.typeEquals(ButtonType.YES) }?.let {
                            ApplicationRestart().restartApp()
                        }
                    }
                }
            }.let {
                addEntry(
                    I18N.getValue("preferences.language.lang"),
                    I18N.getValue("preferences.language.lang.desc"),
                    it
                )
            }

        }
    }
}