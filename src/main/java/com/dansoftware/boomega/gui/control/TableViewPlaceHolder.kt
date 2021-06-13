package com.dansoftware.boomega.gui.control

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.function.Consumer
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

    constructor(tableView: TableView<*>, valueIfEmpty: Supplier<Node>, valueIfNoColumns: Supplier<Node>) : this(tableView) {
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