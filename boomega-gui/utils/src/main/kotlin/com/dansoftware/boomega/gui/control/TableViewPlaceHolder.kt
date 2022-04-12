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

package com.dansoftware.boomega.gui.control

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import java.util.function.Supplier

open class TableViewPlaceHolder(private val tableView: TableView<*>) :
    StackPane() {

    private var valueIfEmpty: Supplier<Node>? = null
    private var valueIfNoColumns: Supplier<Node>? = null

    @Suppress("JoinDeclarationAndAssignment")
    private val noColumns: BooleanBinding

    init {
        this.noColumns = Bindings.isEmpty(tableView.columns)
        this.buildUI()
    }

    constructor(tableView: TableView<*>, valueIfEmpty: Supplier<Node>, valueIfNoColumns: Supplier<Node>) : this(
        tableView
    ) {
        this.valueIfEmpty = valueIfEmpty
        this.valueIfNoColumns = valueIfNoColumns
    }

    constructor(tableView: TableView<*>, valueIfEmpty: () -> String, valueIfNoColumns: () -> String) : this(
        tableView,
        Supplier { Label(valueIfEmpty()) },
        Supplier { Label(valueIfNoColumns()) }
    )

    private fun buildUI() {
        noColumns.addListener { _, _, noColumns ->
            when {
                noColumns -> children.setAll(Group(contentIfNoColumns()))
                else -> children.setAll(Group(contentIfEmpty()))
            }
        }
    }

    protected open fun contentIfEmpty(): Node? {
        return valueIfEmpty?.get()
    }

    protected open fun contentIfNoColumns(): Node? {
        return valueIfNoColumns?.get()
    }
}