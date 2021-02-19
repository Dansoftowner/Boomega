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