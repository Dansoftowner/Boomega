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

class TableViewPlaceHolder(tableView: TableView<*>, valueIfEmpty: Supplier<Node>, valueIfNoColumns: Supplier<Node>) :
    StackPane() {

    @Suppress("JoinDeclarationAndAssignment")
    private val noColumns: BooleanBinding

    init {
        this.noColumns = Bindings.isEmpty(tableView.columns)
        this.buildUI(valueIfEmpty, valueIfNoColumns)
    }

    constructor(tableView: TableView<*>, valueIfEmpty: () -> String, valueIfNoColumns: () -> String) : this(
        tableView,
        Supplier { Label(valueIfEmpty()) },
        Supplier { Label(valueIfNoColumns()) }
    )

    private fun buildUI(valueIfEmpty: Supplier<Node>, valueIfNoColumns: Supplier<Node>) {
        noColumns.addListener { _, _, noColumns ->
            when {
                noColumns -> children.setAll(Group(valueIfNoColumns.get()))
                else -> children.setAll(Group(valueIfEmpty.get()))
            }
        }
    }
}