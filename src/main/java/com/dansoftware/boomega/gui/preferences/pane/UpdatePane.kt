package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.layout.StackPane

class UpdatePane(preferences: Preferences): PreferencesPane(preferences) {
    override val title: String = I18N.getValue("preferences.tab.update")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.UPDATE)

    init {
        initEntries()
    }

    private fun initEntries() {
        CheckBox().apply {
            isSelected = preferences.get(Preferences.Key.SEARCH_UPDATES)
            selectedProperty().addListener { _, _, selected ->
                preferences.editor().put(Preferences.Key.SEARCH_UPDATES, selected)
            }
        }.let { StackPane(it.also { StackPane.setAlignment(it, Pos.CENTER_RIGHT) }) }.let {
            addEntry(
                I18N.getValue("preferences.update.automatic"),
                I18N.getValue("preferences.update.automatic.desc"),
                it
            )
        }
    }


}