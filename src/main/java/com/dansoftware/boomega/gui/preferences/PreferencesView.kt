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

package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.preferences.pane.*
import com.dlsc.workbenchfx.Workbench
import com.dlsc.workbenchfx.model.WorkbenchModule
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image

class PreferencesView(private val preferences: Preferences) : Workbench() {

    private val asContext: Context = Context.from(this)
    private lateinit var tabPane: TabPane

    init {
        styleClass.add("preferences-view")
        initUI()
        initPanes()
    }

    private fun initUI() {
        tabPane = TabPane()
        modules.add(object : WorkbenchModule("", null as Image?) {
            override fun activate(): Node = tabPane
        })
    }

    private fun initPanes() {
        listOf(
            AppearancePane(preferences),
            KeyBindingPane(preferences),
            LanguagePane(asContext, preferences),
            UpdatePane(preferences),
            AdvancedPane(asContext, preferences)
        ).forEach(this::addPane)
    }

    private fun addPane(prefPane: PreferencesPane) {
        tabPane.tabs.add(Tab(prefPane.title).apply {
            isClosable = false
            graphic = prefPane.graphic
            selectedProperty().addListener(object : ChangeListener<Boolean> {
                override fun changed(
                    observable: ObservableValue<out Boolean>,
                    oldValue: Boolean,
                    isSelected: Boolean
                ) {
                    if (isSelected) {
                        this@apply.content = ScrollPane(prefPane.getContent()).apply {
                            isFitToHeight = true
                            isFitToWidth = true
                        }
                        observable.removeListener(this)
                    }
                }
            })
        })
    }
}