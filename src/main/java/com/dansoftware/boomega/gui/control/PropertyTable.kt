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

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.i18n.i18n
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableStringValue
import javafx.scene.Node
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import javafx.scene.control.cell.PropertyValueFactory

open class PropertyTable : TableView<PropertyTable.Entry>() {

    init {
        styleClass.add("property-table")
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        buildColumns()
    }

    private fun buildColumns() {
        columns.add(PropertyNameColumn())
        columns.add(ValueColumn())
    }

    private class PropertyNameColumn : TableColumn<Entry, String>() {
        init {
            text = i18n("prop_table.property")
            cellValueFactory = PropertyValueFactory("propertyName")
            isReorderable = false
            isSortable = false
            setCellFactory {
                object : PropertyTableCell() {

                    init {
                        styleClass.add("property-mark-label")
                    }

                    override fun updateItem(entry: Entry?) {
                        entry?.let {
                            text = entry.propertyName
                        } ?: run {
                            text = null
                            graphic = null
                        }
                    }
                }
            }
        }
    }

    private class ValueColumn : TableColumn<Entry, String>() {
        init {
            text = i18n("prop_table.value")
            isReorderable = false
            isSortable = false
            setCellFactory {
                 object : PropertyTableCell() {
                    override fun updateItem(entry: Entry?) {
                        entry?.let {
                            it.configureTableCell(this)
                        } ?: run {
                            textProperty().unbind()
                            graphicProperty().unbind()
                            text = null
                            graphic = null
                        }
                    }
                }
            }
        }
    }

    private abstract class PropertyTableCell : TableCell<Entry, String>() {
        override fun updateIndex(i: Int) {
            super.updateIndex(i)
            if (i in 0 until tableView.items.size) {
                updateItem(tableView.items[i])
            } else {
                updateItem(null)
            }
        }

        protected abstract fun updateItem(entry: Entry?)
    }

    class Entry(val propertyName: String, val configureTableCell: (TableCell<Entry, String>) -> Unit) {

        constructor(propertyName: String, graphic: Node) :
                this(propertyName, graphic = { graphic })

        constructor(propertyName: String, graphic: () -> Node) :
                this(propertyName, configureTableCell = {
                    it.graphic = graphic()
                })

        constructor(propertyName: String, value: ObservableStringValue) :
                this(propertyName, configureTableCell = {
                    it.graphic = HighlightableLabel().apply {
                        tooltip = Tooltip().also { t -> t.textProperty().bind(textProperty()) }
                        textProperty().bind(Bindings.createStringBinding({ value.get() ?: "-" }, value))
                    }
                })
    }
}