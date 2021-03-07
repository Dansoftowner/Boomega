package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.control.KeyBindDetectionField
import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Node

class KeyBindingPane(preferences: Preferences) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.keybindings")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.KEYBOARD)

    init {
        initEntries()
    }

    private fun initEntries() {
        KeyBindings.allKeyBindings().forEach {
            addKeyDetectionField(
                it.i18nTitle,
                it.i18nDescription,
                it
            )
        }
    }

    private fun addKeyDetectionField(title: String, description: String, keyBinding: KeyBinding) {
        addEntry(
            I18N.getValue(title),
            I18N.getValue(description),
            KeyBindDetectionField(keyBinding.keyCombination).apply {
                this.keyCombinationProperty().addListener { _, _, _ -> KeyBindings.writeTo(preferences) }
                this.keyCombinationProperty().bindBidirectional(keyBinding.keyCombinationProperty)
                keyBinding.keyCombinationProperty.bindBidirectional(this.keyCombinationProperty())
            }
        )
    }
}