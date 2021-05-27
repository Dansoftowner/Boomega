package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.StackPane
import org.controlsfx.control.ToggleSwitch

class UpdatePane(preferences: Preferences) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.update")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.UPDATE)

    override fun buildContent(): Content = object : Content() {
        init {
            buildItems()
        }

        private fun buildItems() {
            items.add(buildAutoSearchToggle())
        }

        private fun buildAutoSearchToggle(): PreferencesControl =
            ToggleControl(
                I18N.getValue("preferences.update.automatic"),
                I18N.getValue("preferences.update.automatic.desc")
            ).apply {
                isSelected = preferences.get(PreferenceKey.SEARCH_UPDATES)
                selectedProperty().addListener { _, _, selected ->
                    preferences.editor().put(PreferenceKey.SEARCH_UPDATES, selected)
                }
            }
    }
}