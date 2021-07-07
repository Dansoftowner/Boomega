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

import javafx.scene.Node

/**
 * Represents a module that can be displayed in the [DatabaseView]'s tab-view.
 */
abstract class Module {

    abstract val name: String
    abstract val icon: Node
    abstract val id: String

    /**
     * Returns true if the module is opened.
     */
    var isOpened: Boolean = false
        private set

    /**
     * Gets called when the modules is not opened yet and updates the [isOpened] value.
     *
     * @return the content to be displayed
     */
    fun activate(): Node =
        buildContent().also {
            isOpened = true
        }

    /**
     * Gets called when the module receives a close-request.
     *
     * @return **true** if the module should be closed; **false** if the module should not be closed
     */
    fun close(): Boolean =
        destroy().also {
            isOpened = !it
        }

    /**
     * Gets called when the module is not opened yet.
     *
     * _Note: It does not update the [isOpened] value. Should be only used by sub-classes!_
     *
     * @return the content do be displayed
     */
    protected abstract fun buildContent(): Node

    /**
     * Gets called when the module receives a close-request
     *
     * _Note: It does not update the [isOpened] value. Should be only used by sub-classes!_
     *
     * @return **true** if the module should be closed, **false** if the module should not be closed
     */
    protected abstract fun destroy(): Boolean

    open fun sendMessage(message: Message) {
    }

    interface Message
}