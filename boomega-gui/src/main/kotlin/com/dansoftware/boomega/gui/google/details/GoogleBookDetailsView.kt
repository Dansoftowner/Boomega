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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.rest.google.books.Volume
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.NodeOrientation
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class GoogleBookDetailsView(private val context: Context) : HBox(15.0) {

    private val volume: ObjectProperty<Volume> = SimpleObjectProperty()

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add("google-book-details-pane")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        minHeight = 0.0
        buildUI()
    }

    constructor(context: Context, volume: Volume) : this(context) {
        this.volume.set(volume)
    }

    private fun buildUI() {
        children.add(ThumbnailArea(context))
        children.add(TabArea())
    }

    fun volumeProperty() = volume

    private inner class TabArea : TabPane() {
        init {
            styleClass.add(JMetroStyleClass.UNDERLINE_TAB_PANE)
            setHgrow(this, Priority.ALWAYS)
            initUI()
            initTabs()
            selectionModel.selectLast()
        }

        private fun initUI() {
            side = Side.BOTTOM
            tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
            initOrientation()
        }

        private fun initTabs() {
            tabs.add(Tab(i18n("google.books.details.sale"), SalePane()))
            tabs.add(
                Tab(
                    i18n("google.books.table.column.desc"),
                    DescriptionPane().apply { volumeProperty().bind(volume) })
            )
            tabs.add(Tab(i18n("google.books.details.info"), VolumeInfoTable(volume)))
        }

        private fun initOrientation() {
            skinProperty().addListener(object : ChangeListener<Skin<*>?> {
                override fun changed(
                    observable: ObservableValue<out Skin<*>?>,
                    oldValue: Skin<*>?,
                    newValue: Skin<*>?
                ) {
                    newValue?.let {
                        lookup(".tab-header-area").nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
                        observable.removeListener(this)
                    }
                }

            })
        }
    }

    private inner class SalePane : ScrollPane() {

        private var table: SaleInfoTable? = null
            get() = field ?: SaleInfoTable(volume).also {
                field = it
            }

        init {
            isFitToWidth = true
            isFitToHeight = true
            initPlaceHolderPolicy()
        }

        private fun initPlaceHolderPolicy() {
            content = buildNotSaleablePlaceHolder()
            volume.addListener { _, _, volume ->
                content = when (volume.saleInfo?.saleability) {
                    Volume.SaleInfo.FOR_SALE -> table!!
                    else -> buildNotSaleablePlaceHolder()
                }
            }
        }

        private fun buildNotSaleablePlaceHolder(): Node =
            StackPane(Label(i18n("google.books.details.notforsale")))
    }

}