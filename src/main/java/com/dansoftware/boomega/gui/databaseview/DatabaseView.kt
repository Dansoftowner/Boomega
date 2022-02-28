/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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
package com.dansoftware.boomega.gui.databaseview

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.ReadOnlyDatabase
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.base.BaseView
import com.dansoftware.boomega.gui.google.GoogleBooksImportModule
import com.dansoftware.boomega.gui.recordview.RecordsViewModule
import com.dansoftware.boomega.plugin.api.ModulePlugin
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.plugin.api.of
import javafx.stage.WindowEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DatabaseView(
    private val preferences: Preferences,
    private val database: Database,
    databaseTracker: DatabaseTracker
) : BaseView() {

    val modules: List<Module> = loadModules()

    val databaseReadOnly: ReadOnlyDatabase
        get() = ReadOnlyDatabase(database)

    val databaseMeta: DatabaseMeta
        get() = database.meta

    init {
        styleClass.add("database-view")
        content = DatabaseViewBase(this, preferences, databaseTracker)
        initSafetyModuleClosePolicy()
    }

    fun openTab(tabItem: TabItem) {
        (content as DatabaseViewBase).openTab(tabItem)
    }

    fun closeTab(tabItem: TabItem) {
        (content as DatabaseViewBase).closeTab(tabItem)
    }

    fun openModuleTab() {
        openTab(ModuleView.getTabItem(this))
    }

    fun openModule(module: Module) {
        openTab(module.getTabItem())
    }

    override fun sendRequest(request: Context.Request) {
        when(request) {
            is ModuleShowRequest<*> ->
                modules.find { it.javaClass == request.classRef }?.let {
                    openModule(it)
                    request.moduleMessage?.let { msg ->
                        it.sendMessage(msg)
                    }
                }
            is TabItemShowRequest -> openTab(request.tabItem)
            is TabItemCloseRequest -> closeTab(request.tabItem)
        }
    }

    private fun initSafetyModuleClosePolicy() {
        val shutdownHook = ShutdownHook()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
        onWindowPresent {
            logger.debug("Window found! Adding event handler for WINDOW_HIDDEN event.")
            it.addEventHandler(WindowEvent.WINDOW_HIDDEN) {
                logger.debug("Closing modules forcefully...")
                closeModulesForcefully()
                Runtime.getRuntime().removeShutdownHook(shutdownHook)
            }
        }
    }

    private fun closeModulesForcefully() {
        modules.filter(Module::isOpened).forEach {
            try {
                it.close()
            } catch (e: Exception) {
                logger.error("Received exception when trying to close the module with the id: '{}'", it.id, e)
            }
        }
    }

    private fun loadModules(): List<Module> {
        return loadBuiltInModules().plus(loadPluginModules())
            .onEach { logger.info("Found module: '{}'", it::class.java.name) }
            .toList()
    }

    private fun loadBuiltInModules(): Sequence<Module> {
        return sequenceOf(
            RecordsViewModule(this, preferences, database),
            GoogleBooksImportModule(this, preferences)
        )
    }

    private fun loadPluginModules(): Sequence<Module> {
        logger.debug("Checking plugins for modules...")
        return get(PluginService::class).of(ModulePlugin::class).asSequence()
            .map { it.getModule(this, databaseReadOnly) }
    }

    private inner class ShutdownHook : Thread() {
        override fun run() {
            logger.debug("Shutdown hook: closing modules forcefully...")
            closeModulesForcefully()

            // make sure that preferences are committed
            preferences.editor().commit()
        }
    }

    class TabItemShowRequest(val tabItem: TabItem) : Context.Request

    class TabItemCloseRequest(val tabItem: TabItem) : Context.Request

    class ModuleShowRequest<M : Module>(val classRef: Class<M>) : Context.Request {

        var moduleMessage: Module.Message? = null

        constructor(classRef: Class<M>, moduleMessage: Module.Message) : this(classRef) {
            this.moduleMessage = moduleMessage
        }

    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DatabaseView::class.java)
    }
}