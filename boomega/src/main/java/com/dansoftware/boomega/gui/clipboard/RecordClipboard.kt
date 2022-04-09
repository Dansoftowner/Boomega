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

package com.dansoftware.boomega.gui.clipboard

import com.dansoftware.boomega.database.api.data.Record
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.util.*
import java.util.function.Consumer

object RecordClipboard {

    private val identifier: ObjectProperty<Any> = SimpleObjectProperty()
    private val action: ObjectProperty<Action> = SimpleObjectProperty()
    private val items: ObservableList<Record> = FXCollections.observableArrayList()
    private val callback: ObjectProperty<ClipboardPush?> = SimpleObjectProperty()
    private val empty: BooleanProperty = SimpleBooleanProperty().apply { bind(Bindings.isEmpty(items)) }

    @JvmStatic
    fun pushItems(identifier: Any, action: Action, items: List<Record>): ClipboardPush =
        ArrayList(items).let { records ->
            ClipboardPushImpl(identifier, records, action).also { push ->
                RecordClipboard.identifier.get()?.let {
                    callback.get()?.let {
                        it.onOverridden?.accept(it)
                    }
                }
                RecordClipboard.identifier.set(identifier)
                RecordClipboard.action.set(action)
                RecordClipboard.items.setAll(records)
                callback.set(push)
            }
        }

    @JvmStatic
    fun pullContent(): ClipboardContent =
        ClipboardContent(identifier.get(), Collections.unmodifiableList(ArrayList(items)).also {
            callback.get()?.let { it.onPulled?.accept(it) }
            if (action.get() == Action.CUT) {
                items.clear()
                callback.set(null)
                identifier.set(null)
            }
        })


    @JvmStatic
    fun emptyProperty(): ReadOnlyBooleanProperty = empty

    @JvmStatic
    fun actionProperty(): ReadOnlyObjectProperty<Action?> = action

    @JvmStatic
    fun identifierProperty(): ReadOnlyObjectProperty<Any> = identifier

    @JvmStatic
    fun items(): ObservableList<Record> = items

    enum class Action {
        CUT, COPY
    }

    class ClipboardContent(val identifier: Any, val items: List<Record>)

    interface ClipboardPush {
        val identifier: Any
        val items: List<Record>
        val action: Action
        var onOverridden: Consumer<ClipboardPush>?
        var onPulled: Consumer<ClipboardPush>?

        fun onOverridden(value: Consumer<ClipboardPush>?) {
            this.onOverridden = value
        }

        fun onPulled(value: Consumer<ClipboardPush>?) {
            this.onPulled = value
        }
    }

    private class ClipboardPushImpl(
        override val identifier: Any,
        override val items: List<Record>,
        override val action: Action
    ) : ClipboardPush {
        override var onOverridden: Consumer<ClipboardPush>? = null
        override var onPulled: Consumer<ClipboardPush>? = null
    }
}