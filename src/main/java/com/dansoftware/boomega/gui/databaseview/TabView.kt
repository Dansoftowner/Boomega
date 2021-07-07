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

package com.dansoftware.boomega.gui.databaseview

import javafx.collections.ListChangeListener
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The [TabView] is a tab-area used by the [DatabaseView].
 * Allows to open tabs represented as [TabItem]s.
 *
 * @param baseTabItem the tab item that will be shown initially and if other tabs are closed
 */
class TabView(private val baseTabItem: TabItem) : StackPane() {

    private val idsWithItems: MutableMap<String, TabItem> = HashMap()
    private val tabPane: TabPane = buildTabPane()

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add("tab-view")
        buildUI()
        initModuleTab()
    }

    private fun buildUI() {
        children.add(tabPane)
    }

    private fun buildTabPane() = TabPane().apply {
        tabDragPolicy = TabPane.TabDragPolicy.REORDER
        tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
    }

    private fun initModuleTab() {
        openTab(baseTabItem)
        tabPane.tabs.addListener(ListChangeListener {
            if (tabPane.tabs.isEmpty()) {
                openTab(baseTabItem)
            }
        })
    }

    fun openTab(item: TabItem) {
        if (idsWithItems.containsKey(item.id)) {
            logger.debug("Tab with id '{}' found", item.id)
            selectTab(idsWithItems[item.id]!!)
        } else {
            logger.debug("Tab with id '{}' not found", item.id)
            idsWithItems[item.id] = item
            makeTab(item) {
                idsWithItems.remove(item.id)
                logger.debug("Tab with id '{}' removed")
            }
        }
    }

    private fun selectTab(tabItem: TabItem) {
        val tab = tabPane.tabs.filterIsInstance<TabImpl>().find { it.tabItem == tabItem }
        tab?.let(tabPane.selectionModel::select)
    }

    @Suppress("NullableBooleanElvis")
    private fun makeTab(item: TabItem, onClose: EventHandler<Event>) {
        val tab = TabImpl(item).apply {
            setOnCloseRequest { event ->
                when {
                    item.onCloseRequest?.invoke(this.content) ?: true -> onClose.handle(event)
                    else -> event.consume()
                }
            }
        }
        tabPane.tabs.add(tab)
        tabPane.selectionModel.select(tab)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TabView::class.java)
    }

    private class TabImpl(val tabItem: TabItem) : Tab() {
        init {
            this.text = tabItem.title
            this.graphic = tabItem.graphicFactory()
            this.content = tabItem.contentFactory()
        }
    }
}