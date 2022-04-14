/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.gui.action.FullScreenAction
import com.dansoftware.boomega.gui.action.MaximizeWindowAction
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.i18n.api.i18n
import javafx.collections.ListChangeListener
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.stage.Stage
import javafx.stage.Window
import java.lang.ref.WeakReference

class WindowMenu(private val context: Context) : Menu(i18n("menubar.menu.window")) {

    private val windowsChangeOperator by lazy {
        object {
            fun onWindowsAdded(windows: List<Window>) {
                windows.filter { it is Stage && it.owner == null }.map { it as Stage }.forEach { window ->
                    this@WindowMenu.menuItem(CheckMenuItem().also {
                        it.userData = WeakReference<Window>(window)
                        it.textProperty().bind(window.titleProperty())
                        window.focusedProperty().addListener { _, _, yes ->
                            it.isSelected = yes
                        }
                        it.setOnAction { window.toFront() }
                    })
                }
            }

            fun onWindowsRemoved(windows: List<Window>) {
                windows.filter { it is Stage && it.owner == null }.forEach { window ->
                    val iterator = this@WindowMenu.items.iterator()
                    while (iterator.hasNext()) {
                        val element = iterator.next()
                        if (element.userData is WeakReference<*>) {
                            when {
                                element.userData === null || (element.userData as WeakReference<*>).get() == window ->
                                    iterator.remove()
                            }
                        }
                    }
                }
            }
        }
    }

    private val windowListChangeListener by lazy {
        ListChangeListener<Window> { change ->
            while (change.next()) {
                when {
                    change.wasAdded() -> windowsChangeOperator.onWindowsAdded(change.addedSubList)
                    change.wasRemoved() -> windowsChangeOperator.onWindowsRemoved(change.removed)
                }
            }
        }
    }

    init {
        this.menuItem(maximizeMenuItem()).menuItem(fullScreenMenuItem()).separator()
        windowsChangeOperator.onWindowsAdded(Window.getWindows())
        Window.getWindows().addListener(WeakWindowsChangeListener(WeakReference(windowListChangeListener)))
    }

    private fun fullScreenMenuItem() =
        menuItemOf(FullScreenAction, context, ::CheckMenuItem).apply {
            context.onWindowPresent { window ->
                if (window is Stage)
                    window.fullScreenProperty().addListener { _, _, isFullScreen ->
                        selectedProperty().set(isFullScreen)
                    }
            }
        }

    private fun maximizeMenuItem() =
        menuItemOf(MaximizeWindowAction, context, ::CheckMenuItem).apply {
            context.onWindowPresent { window ->
                if (window is Stage) {
                    window.maximizedProperty().addListener { _, _, isMaximized ->
                        selectedProperty().set(isMaximized)
                    }
                }
            }
        }


    private class WeakWindowsChangeListener(val weakReference: WeakReference<ListChangeListener<Window>>) :
        ListChangeListener<Window> {
        override fun onChanged(c: ListChangeListener.Change<out Window>?) {
            weakReference.get()?.onChanged(c) ?: Window.getWindows().removeListener(this)
        }
    }
}