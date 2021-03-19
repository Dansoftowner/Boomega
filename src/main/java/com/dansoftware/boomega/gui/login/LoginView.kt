package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.appdata.logindata.LoginData
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.ContextTransformable
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.info.InformationActivity
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.boomega.gui.preferences.PreferencesActivity
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.asKeyEvent
import com.dansoftware.boomega.gui.util.keyCombination
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.concurrent.ExploitativeExecutor
import com.dlsc.workbenchfx.SimpleHeaderView
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableStringValue
import javafx.concurrent.Task
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCodeCombination

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the [Database] object.
 *
 * @author Daniel Gyorffy
 */
class LoginView(
    private val preferences: Preferences,
    tracker: DatabaseTracker,
    loginData: LoginData,
    databaseLoginListener: DatabaseLoginListener
) : SimpleHeaderView<LoginViewBase>(
    I18N.getValue("database.auth"),
    MaterialDesignIconView(MaterialDesignIcon.LOGIN)
), ContextTransformable {

    private val asContext: Context = Context.from(this)
    private val loginViewBase: LoginViewBase =
        LoginViewBase(asContext, preferences, tracker, loginData, databaseLoginListener)
    private val createdDatabase: ObjectProperty<Database> = SimpleObjectProperty()

    init {
        content = loginViewBase
        buildToolbar()
    }

    private fun buildToolbar() {
        toolbarControlsRight.add(buildOptionsItem())
        toolbarControlsRight.add(buildInfoItem())
    }

    val loginData: LoginData
        get() = loginViewBase.loginData

    fun createdDatabaseProperty(): ReadOnlyObjectProperty<Database> {
        return createdDatabase
    }

    override fun getContext(): Context {
        return asContext
    }

    fun titleProperty(): ObservableStringValue {
        return loginViewBase.titleProperty()
    }

    private fun buildInfoItem(): ToolbarItem = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.INFORMATION)) {
        InformationActivity(context).show()
    }

    private fun buildOptionsItem() = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.SETTINGS)).apply {
        items.addAll(
            MenuItem(I18N.getValue("update.search"), MaterialDesignIconView(MaterialDesignIcon.UPDATE)).action {
                val task = object : Task<UpdateSearcher.UpdateSearchResult>() {
                    init {
                        setOnSucceeded {
                            context.stopProgress()
                            UpdateActivity(context, it.source.value as UpdateSearcher.UpdateSearchResult).show()
                        }
                        setOnRunning { context.showIndeterminateProgress() }
                    }

                    override fun call() = UpdateSearcher.defaultInstance().search()
                }
                ExploitativeExecutor.submit(task)
            },

            MenuItem(I18N.getValue("plugin.manager.open"), FontAwesomeIconView(FontAwesomeIcon.PLUG)).action {
                PluginManagerActivity().show(context.contextWindow)
            },

            MenuItem(I18N.getValue("app.settings"), MaterialDesignIconView(MaterialDesignIcon.SETTINGS)).action {
                PreferencesActivity(preferences).show(context.contextWindow)
            }
        )
    }
}