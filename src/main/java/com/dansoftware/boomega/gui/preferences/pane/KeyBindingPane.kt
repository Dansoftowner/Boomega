package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.gui.control.KeyBindDetectionField
import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.css.PseudoClass
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KeyBindingPane(preferences: Preferences) : PreferencesPane(preferences) {

    override val title: String = I18N.getValue("preferences.tab.keybindings")
    override val graphic: Node = MaterialDesignIconView(MaterialDesignIcon.KEYBOARD)

    override fun buildContent(): Content = object : Content() {
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
            addRestoreButton()
        }

        private fun addRestoreButton() {
            Button(I18N.getValue("preferences.keybindings.restore_defaults")).apply {
                setOnAction {
                    //TODO: confirmation dialog
                    KeyBindings.allKeyBindings().forEach {
                        it.keyCombinationProperty.set(it.defaultKeyCombination)
                    }
                }

                //just for adding default button styles
                pseudoClassStateChanged(PseudoClass.getPseudoClass("default"), true)
            }.let { StackPane(it) }.let(::addSimpleControl)
        }

        private fun addKeyDetectionField(title: String, description: String, keyBinding: KeyBinding) {
            addEntry(
                I18N.getValue(title),
                I18N.getValue(description),
                KeyBindDetectionField(keyBinding.keyCombination).apply {
                    this.keyCombinationProperty().addListener { _, _, _ ->
                        logger.debug("Saving key combination to preferences...")
                        KeyBindings.writeTo(preferences)
                    }
                    this.keyCombinationProperty().bindBidirectional(keyBinding.keyCombinationProperty)
                    keyBinding.keyCombinationProperty.bindBidirectional(this.keyCombinationProperty())
                }
            )
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeyBindingPane::class.java)
    }
}