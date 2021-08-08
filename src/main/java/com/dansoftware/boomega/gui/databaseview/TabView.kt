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

import com.dansoftware.boomega.i18n.I18N
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ListChangeListener
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.*
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
                logger.debug("Tab with id '{}' removed", item.id)
            }
        }
    }

    private fun selectTab(tabItem: TabItem) {
        tabItem.tabImpl?.let(tabPane.selectionModel::select)
    }

    @Suppress("NullableBooleanElvis")
    private fun makeTab(item: TabItem, onClose: EventHandler<Event>) {
        val tab = TabImpl(this, item).apply {
            setOnCloseRequest { event ->
                when {
                    item.onClose(this.content) -> onClose.handle(event)
                    else -> event.consume()
                }
            }
        }
        tabPane.tabs.add(tab)
        tabPane.selectionModel.select(tab)
    }

    fun closeTab(item: TabItem) {
        item.tabImpl?.let(::closeTabImpl)
    }

    private fun closeTabImpl(tab: TabImpl) {
        if (tab.tabItem != baseTabItem) {
            val event = Event(Tab.TAB_CLOSE_REQUEST_EVENT)
            tab.onCloseRequest.handle(event)
            if (!event.isConsumed) {
                tabPane.tabs.remove(tab)
            }
        }
    }

    private val TabItem.tabImpl: TabImpl?
        get() = tabPane.tabs.filterIsInstance<TabImpl>().find { it.tabItem == this }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TabView::class.java)
    }

    private class TabImpl(val tabView: TabView, val tabItem: TabItem) : Tab() {

        private val indexProperty = SimpleIntegerProperty().also { property ->
            val tabs = tabView.tabPane.tabs
            tabs.addListener(object : ListChangeListener<Tab> {
                override fun onChanged(c: ListChangeListener.Change<out Tab>?) {
                    when {
                        tabs.contains(this@TabImpl) ->
                            property.set(tabs.indexOf(this@TabImpl))
                        else ->
                            tabs.removeListener(this)
                    }
                }
            })
        }

        init {
            this.text = tabItem.title
            this.graphic = tabItem.graphic
            this.content = tabItem.content
            this.tooltip = buildTooltip()
            this.contextMenu = buildContextMenu()
        }

        private fun buildTooltip() =
            Tooltip().apply {
                textProperty().bind(this@TabImpl.textProperty())
            }

        private fun buildContextMenu() =
            ContextMenu(
                buildCloseItem(),
                buildCloseOtherTabsItem(),
                buildCloseAllTabsItem(),
                buildCloseTabsToTheLeft(),
                buildCloseTabsToTheRight()
            )

        private fun buildCloseItem() =
            MenuItem(I18N.getValue("database_view.tab_menu.close")).apply {
                isDisable = (tabView.baseTabItem == tabItem)
                setOnAction {
                    tabView.closeTabImpl(this@TabImpl)
                }
            }

        private fun buildCloseOtherTabsItem() =
            MenuItem(I18N.getValue("database_view.tab_menu.close_other")).apply {
                disableProperty().bind(
                    Bindings.size(tabView.tabPane.tabs)
                        .greaterThanOrEqualTo(if (tabItem == tabView.baseTabItem) 2 else 3)
                        .not()
                )
                setOnAction {
                    tabView.tabPane.tabs
                        .filterIsInstance<TabImpl>()
                        .filter { it != this@TabImpl }
                        .forEach(tabView::closeTabImpl)
                }
            }

        private fun buildCloseAllTabsItem() =
            MenuItem(I18N.getValue("database_view.tab_menu.close_all")).apply {
                disableProperty().bind(
                    Bindings.size(tabView.tabPane.tabs)
                        .greaterThanOrEqualTo(if (tabItem == tabView.baseTabItem) 2 else 1)
                        .not()
                )
                setOnAction {
                    tabView.tabPane.tabs
                        .filterIsInstance<TabImpl>()
                        .forEach(tabView::closeTabImpl)
                }
            }

        @Suppress("UsePropertyAccessSyntax")
        private fun buildCloseTabsToTheLeft() =
            MenuItem(I18N.getValue("database_view.tab_menu.close_left")).apply {
                disableProperty().bind(indexProperty.greaterThan(0).not())
                setOnAction {
                    val tabs = tabView.tabPane.tabs.filterIsInstance<TabImpl>()
                    val currentIndex = tabs.indexOf(this@TabImpl)
                    tabs.dropLast(tabs.size - currentIndex).forEach(tabView::closeTabImpl)
                }
            }

        private fun buildCloseTabsToTheRight() =
            MenuItem(I18N.getValue("database_view.tab_menu.close_right")).apply {
                disableProperty().bind(
                    Bindings.valueAt(tabView.tabPane.tabs, indexProperty.add(1)).isNull
                )
                setOnAction {
                    val tabs = tabView.tabPane.tabs.filterIsInstance<TabImpl>()
                    val currentIndex = tabs.indexOf(this@TabImpl)
                    tabs.drop(currentIndex + 1).forEach(tabView::closeTabImpl)
                }
            }

    }
}