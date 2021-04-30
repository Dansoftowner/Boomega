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

package com.dansoftware.boomega.gui.record.googlebook

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.googlebooks.SingleGoogleBookQuery
import com.dansoftware.boomega.googlebooks.Volume
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.googlebooks.GoogleBookDetailsPane
import com.dansoftware.boomega.gui.record.show.RecordTable
import com.dansoftware.boomega.gui.util.I18NButtonTypes
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor.submit
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.util.concurrent.CachedExecutor

/**
 * A [GoogleBookConnectionView] is a panel that shows the connection between
 * a Google Book volume and a [Record]. It allows to remove, modify and create
 * connection.
 *
 * @author Daniel Gyorffy
 */
class GoogleBookConnectionView(
    private val context: Context,
    private val database: Database,
    items: List<Record>
) : VBox() {

    private val detailsPane: GoogleBookDetailsPane = GoogleBookDetailsPane(context)

    var items: List<Record> = emptyList()
        set(value) {
            field = value
            handleNewItems(value)

        }

    private var lastTask: VolumePullTask? = null
        set(value) {
            field?.cancel()
            field = value
        }

    init {
        styleClass.add("google-book-dock")
        buildUI()
    }

    private fun handleNewItems(items: List<Record>) {
        retrieveGoogleBookHandle(items)?.let {
            CachedExecutor.submit(VolumePullTask(it))
        }
    }

    //private fun displayVolume(volume)

    fun setOnRefreshed(value: Runnable) {

    }

    private fun buildUI() {
        children.add(detailsPane)
        children.add(buildRemoveButton())
    }

    private fun buildRemoveButton(): Button? {
        val button = Button(I18N.getValue("google.books.dock.remove_connection"))
        button.styleClass.add("remove-button")
        button.prefWidthProperty().bind(widthProperty())
        button.setOnAction {
            context.showDialog(
                I18N.getValue("google.books.dock.remove.confirmation.title", items.size),
                buildPreviewTable(items),
                {
                // TODO: remove task
                /*if (it.typeEquals(ButtonType.YES)) {
                        submit(buildConnectionRemoveTask())
                    }*/
                },
                I18NButtonTypes.CANCEL,
                I18NButtonTypes.YES
            )
        }
        return button
    }

    private fun buildPreviewTable(items: List<com.dansoftware.boomega.db.data.Record>): RecordTable? {
        val recordTable = RecordTable(0)
        recordTable.addColumn(RecordTable.ColumnType.INDEX_COLUMN)
        recordTable.addColumn(RecordTable.ColumnType.TYPE_INDICATOR_COLUMN)
        recordTable.addColumn(RecordTable.ColumnType.AUTHOR_COLUMN)
        recordTable.addColumn(RecordTable.ColumnType.TITLE_COLUMN)
        recordTable.items.setAll(items)
        recordTable.prefHeight = 200.0
        return recordTable
    }

    private fun retrieveGoogleBookHandle(items: List<com.dansoftware.boomega.db.data.Record>?): String? {
        if (items == null) return null
        val distinctHandles = items.stream()
            .map(com.dansoftware.boomega.db.data.Record::serviceConnection)
            .map { it?.googleBookHandle }
            .distinct()
            .collect(Collectors.toList())
        return if (distinctHandles.size == 1) distinctHandles[0] else null
    }

    private fun showProgress() {

    }

    private fun stopProgress() {

    }

    private inner class VolumePullTask(private val googleHandle: String) : Task<Volume?>() {
        private fun initEventHandlers() {
            this.onRunning = EventHandler { e: WorkerStateEvent? -> showProgress() }
            this.onSucceeded = EventHandler { event: WorkerStateEvent? ->
                logger.debug("Pull task succeeded.")
                stopProgress()
                detailsPane.volumeProperty().set(value)
            }
            this.onFailed = EventHandler { event: WorkerStateEvent ->
                logger.error("Pull task failed.", event.source.exception)
                stopProgress()
                //TODO: error place holder
            }
        }

        override fun call(): Volume {
            logger.debug("Starting pull task...")
            return SingleGoogleBookQuery(googleHandle).load()
        }

        init {
            initEventHandlers()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBookConnectionView::class.java)
    }
}