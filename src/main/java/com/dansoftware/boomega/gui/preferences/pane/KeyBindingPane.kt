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

package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.Preferences
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
            buildItems()
        }

        private fun buildItems() {
            KeyBindings.allKeyBindings().forEach { items.add(buildKeyBindDetectionControl(it)) }
            items.add(buildRestoreButton())
        }

        private fun buildRestoreButton(): PreferencesControl =
            Button(I18N.getValue("preferences.keybindings.restore_defaults")).run {
                setOnAction {
                    //TODO: confirmation dialog
                    KeyBindings.allKeyBindings().forEach {
                        it.keyCombinationProperty.set(it.defaultKeyCombination)
                    }
                }

                //just for adding default button styles
                pseudoClassStateChanged(PseudoClass.getPseudoClass("default"), true)

                SimpleControl(StackPane(this))
            }

        private fun buildKeyBindDetectionControl(keyBinding: KeyBinding): PreferencesControl =
            PairControl(
                I18N.getValue(keyBinding.i18nTitle),
                I18N.getValue(keyBinding.i18nDescription),
                KeyBindDetectionField(keyBinding.keyCombination).apply {
                    this.keyCombinationProperty().addListener { _, _, combination ->
                        keyBinding.keyCombinationProperty.set(combination)
                        logger.debug("Saving key combination to preferences...")
                        KeyBindings.writeTo(preferences)
                    }
                    this.keyCombinationProperty().bindBidirectional(keyBinding.keyCombinationProperty)
                    //keyBinding.keyCombinationProperty.bindBidirectional(this.keyCombinationProperty())
                }
            )

    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeyBindingPane::class.java)
    }
}