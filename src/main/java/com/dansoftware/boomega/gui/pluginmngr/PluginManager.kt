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

package com.dansoftware.boomega.gui.pluginmngr

import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.ContextTransformable
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.SimpleHeaderView
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.StackPane
import java.io.File

/**
 * A [PluginManager] is a place where the user can add/remove application plugins.
 *
 * @author Daniel Gyorffy
 */
class PluginManager(pluginFiles: List<File>) :
    SimpleHeaderView<StackPane>(
        I18N.getValue("plugin.manager"),
        FontAwesomeIconView(FontAwesomeIcon.PLUG)
    ),
    ContextTransformable {

    private val asContext: Context = Context.from(this)
    private val pluginList: ObservableList<File> = FXCollections.observableArrayList(pluginFiles)

    init {
        this.content = StackPane(PluginTable(asContext, pluginList))
        this.buildToolbarControls()
    }

    private fun buildToolbarControls() {
        //Add item
        toolbarControlsRight.add(
            ToolbarItem(
                I18N.getValue("plugin.module.adder"),
                MaterialDesignIconView(MaterialDesignIcon.PLUS)
            ).also {
                it.setOnClick { _ ->
                    when (this.content) {
                        is PluginAdderPane -> {
                            this.content = StackPane(PluginTable(asContext, pluginList))
                            it.graphic = MaterialDesignIconView(MaterialDesignIcon.PLUS)
                            it.text = I18N.getValue("plugin.module.adder")
                        }
                        else -> {
                            this.content = PluginAdderPane(asContext, pluginList)
                            it.graphic = MaterialDesignIconView(MaterialDesignIcon.VIEW_LIST)
                            it.text = I18N.getValue("plugin.module.list")
                        }
                    }
                }
            })
    }

    override fun getContext(): Context {
        return asContext
    }
}