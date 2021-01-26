package com.dansoftware.libraryapp.gui.login

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.info.InformationActivity
import com.dansoftware.libraryapp.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.libraryapp.gui.updatedialog.UpdateActivity
import com.dansoftware.libraryapp.i18n.I18N
import com.dansoftware.libraryapp.update.UpdateSearcher
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node

/**
 * Utility class for building the drop-down menu for the [LoginView].
 *
 * @author Daniel Gyorffy
 */
private class ToolbarItemsBuilder(val context: Context) {

    fun build(): Array<ToolbarItem> {
        return arrayOf(menuToolbarItem(), infoToolbarItem())
    }

    private fun infoToolbarItem(): ToolbarItem = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.INFORMATION)) {
        InformationActivity(context).show()
    }

    private fun menuToolbarItem(): ToolbarItem = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.SETTINGS),
        MenuItem(I18N.getGeneralValue("update.search"), MaterialDesignIconView(MaterialDesignIcon.UPDATE)) {
            val task = object : Task<UpdateSearcher.UpdateSearchResult>() {
                override fun call() = UpdateSearcher.defaultInstance().search()
            }
            task.setOnSucceeded {
                context.stopProgress()
                UpdateActivity(context, it.source.value as UpdateSearcher.UpdateSearchResult).show()
            }
            task.setOnRunning { context.showIndeterminateProgress() }
            Thread(task).start()
        },
        MenuItem(I18N.getGeneralValue("plugin.manager.open"), FontAwesomeIconView(FontAwesomeIcon.PLUG)) {
            PluginManagerActivity().show(context.contextWindow)
        },
        MenuItem(I18N.getGeneralValue("app.settings"), MaterialDesignIconView(MaterialDesignIcon.SETTINGS)) {
            // TODO: Showing settings window
        }
    )


    private class MenuItem(text: String, graphic: Node, onAction: EventHandler<ActionEvent>) :
        javafx.scene.control.MenuItem(text, graphic) {
        init {
            this.onAction = onAction
        }
    }

}