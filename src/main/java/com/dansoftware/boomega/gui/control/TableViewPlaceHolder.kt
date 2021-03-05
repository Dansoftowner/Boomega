package com.dansoftware.boomega.gui.control

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class TableViewPlaceHolder(tableView: TableView<*>, valueIfEmpty: () -> String, valueIfNoColumns: () -> String) :
    StackPane() {

    @Suppress("JoinDeclarationAndAssignment")
    private val noColumns: BooleanBinding

    init {
        this.noColumns = Bindings.isEmpty(tableView.columns)
        this.buildUI(valueIfEmpty, valueIfNoColumns)
    }

    private fun buildUI(valueIfEmpty: () -> String, valueIfNoColumns: () -> String) {
        children.add(Group(VBox(
            // TODO: GRAPHIC,
            Label().also {
                noColumns.addListener { _, _, noColumns: Boolean ->
                    it.text = when {
                        noColumns -> valueIfNoColumns()
                        else -> valueIfEmpty()
                    }
                }
            }
        )))
    }
}