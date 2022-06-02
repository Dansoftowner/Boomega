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

package com.dansoftware.boomega.gui.recordview.connection

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.google.details.GoogleBookDetailsView
import com.dansoftware.boomega.gui.recordview.RecordTable
import com.dansoftware.boomega.gui.recordview.util.MultipleSelectionPlaceHolder
import com.dansoftware.boomega.gui.util.I18NButtonTypes
import com.dansoftware.boomega.gui.util.asCentered
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.rest.google.books.GoogleBooksQuery
import com.dansoftware.boomega.rest.google.books.Volume
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import jfxtras.styles.jmetro.JMetroStyleClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

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
    selectedItems: ObservableList<Record>
) : StackPane() {

    private val vBox: VBox = buildVBox()
    private val detailsPane: GoogleBookDetailsView = GoogleBookDetailsView(context)
    private val volumeCache: Cache<String, Volume> = buildCache()

    var onRefreshed: Runnable? = null

    private var items: List<Record> = emptyList()
        set(value) {
            field = value
            handleNewItems(value)
        }

    private var currentGoogleHandle: String? = null
    private var currentVolume: Volume?
        get() = detailsPane.volumeProperty().get()
        set(value) {
            detailsPane.volumeProperty().set(value)
        }

    private var lastTask: VolumePullTask? = null
        set(value) {
            field?.cancel()
            field = value
        }

    init {
        initListObserver(selectedItems)
        buildUI()
        items = selectedItems
    }

    private fun initListObserver(selectedItems: ObservableList<Record>) {
        selectedItems.addListener(ListChangeListener { items = selectedItems })
    }

    fun removeConnectionRequest() {
        context.showDialog(
            i18n("google.books.dock.remove.confirmation.title", items.size),
            buildPreviewTable(items),
            {
                if (it.typeEquals(ButtonType.YES)) {
                    get(ExecutorService::class, "cachedExecutor").submit(ConnectionRemoveTask())
                }
            },
            I18NButtonTypes.CANCEL,
            I18NButtonTypes.YES
        )
    }

    fun isEmpty(): ObservableBooleanValue = detailsPane.parentProperty().isNull

    private fun buildVBox() = VBox().apply { minHeight = 0.0 }

    private fun buildUI() {
        children.add(vBox)
    }

    private fun buildCache(): Cache<String, Volume> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    private fun handleNewItems(items: List<Record>) {
        retrieveGoogleBookHandle(items)?.let {
            normalUI()
            pullVolume(it)
        } ?: when (items.size) {
            1 -> noConnectionUI()
            else -> multipleSelectionUI()
        }
    }

    private fun pullVolume(handle: String) {
        VolumePullTask(handle).also { lastTask = it }.start()
    }

    private fun refresh() {
        items = items
        onRefreshed?.run()
    }

    private fun normalUI() {
        vBox.children.setAll(detailsPane)
    }

    private fun noConnectionUI() {
        vBox.children.setAll(NoConnectionPlaceHolder())
    }

    private fun multipleSelectionUI() {
        vBox.children.setAll(MultipleSelectionPlaceHolder())
    }

    private fun errorUI(e: Throwable) {
        vBox.children.setAll(ErrorPlaceHolder(e))
    }

    private fun buildPreviewTable(records: List<Record>) =
        RecordTable(0).apply {
            addColumnType(RecordTable.INDEX_COLUMN)
            addColumnType(RecordTable.TYPE_INDICATOR_COLUMN)
            addColumnType(RecordTable.AUTHOR_COLUMN)
            addColumnType(RecordTable.TITLE_COLUMN)
            items.setAll(records)
            prefHeight = 200.0
        }

    private fun retrieveGoogleBookHandle(items: List<Record>?): String? {
        return items?.map(Record::serviceConnection)
            ?.map { it?.googleBookHandle }
            ?.distinct()
            ?.singleOrNull()
    }

    private fun showProgress() {
        vBox.children.add(0, ProgressBar().apply {
            progress = ProgressIndicator.INDETERMINATE_PROGRESS
        })
    }

    private fun stopProgress() {
        vBox.children.removeIf { it is ProgressBar }
    }

    private inner class VolumePullTask(private val googleHandle: String) : Task<Volume?>() {

        init {
            initEventHandlers()
        }

        private fun initEventHandlers() {
            this.setOnRunning { showProgress() }
            this.setOnSucceeded {
                logger.debug("Pull task succeeded.")
                stopProgress()
                currentGoogleHandle = googleHandle
                currentVolume = value
            }
            this.setOnFailed {
                logger.error("Pull task failed.", it.source.exception)
                stopProgress()
                errorUI(it.source.exception)
            }
        }

        override fun call(): Volume {
            logger.debug("Starting pull task...")
            return GoogleBooksQuery.byId(googleHandle).load()
        }

        fun start() {
            cacheExisting()
            volumeCache.getIfPresent(googleHandle)?.let {
                logger.debug("Found cache for: '{}'", googleHandle)
                currentGoogleHandle = googleHandle
                currentVolume = it
            } ?: get(ExecutorService::class, "cachedExecutor").submit(this)
        }

        private fun cacheExisting() {
            currentGoogleHandle?.let {
                logger.debug("Creating cache for: '{}'...", it)
                volumeCache.put(it, currentVolume)
            }
        }
    }

    private inner class ConnectionRemoveTask() : Task<Unit>() {

        init {
            setOnRunning { context.showIndeterminateProgress() }
            setOnFailed {
                context.stopProgress()
                //TODO: error dialog
                logger.error("Couldn't remove Google Book connection", it.source.exception)
                onRefreshed?.run()
            }
            setOnSucceeded {
                context.stopProgress()
                onRefreshed?.run()
                noConnectionUI()
                context.showInformationNotification(
                    i18n("google.books.dock.success_unjoin.title"),
                    null,
                    Duration.seconds(2.0)
                )
            }
        }

        override fun call() {
            items.stream()
                .peek { it.serviceConnection?.googleBookHandle = null }
                .forEach(database::updateRecord)
        }
    }

    private inner class ErrorPlaceHolder(private val exception: Throwable) : StackPane() {
        init {
            VBox.setVgrow(this, Priority.ALWAYS)
            styleClass.add(JMetroStyleClass.BACKGROUND)
            styleClass.add("error-place-holder")
            buildUI()
        }

        private fun buildUI() {
            children.add(
                Group(
                    VBox(
                        20.0,
                        buildLabel(),
                        buildDetailsButton().asCentered()
                    )
                )
            )
        }

        private fun buildLabel() =
            HBox(5.0,
                icon("warning-icon").asCentered(),
                Label(i18n("google.books.dock.placeholder.error")).asCentered()
            )


        private fun buildDetailsButton() =
            Button(
                i18n("google.books.dock.placeholder.error.details"),
                icon("details-icon")
            ).apply {
                setOnAction {
                    context.showErrorDialog(
                        "",
                        "",
                        exception as Exception?
                    )
                }
            }
    }

    private inner class NoConnectionPlaceHolder() : StackPane() {

        init {
            VBox.setVgrow(this, Priority.ALWAYS)
            styleClass.add(JMetroStyleClass.BACKGROUND)
            styleClass.add("no-connection-place-holder")
            buildUI()
        }

        private fun buildUI() {
            children.add(buildCenterBox())
        }

        private fun buildCenterBox() =
            Group(
                VBox(20.0,
                    buildLabel().asCentered(),
                    buildConnectionButton()
                )
            )

        private fun buildLabel() =
            HBox(5.0,
                icon("link-off-icon"),
                Label(i18n("google.books.dock.placeholder.noconn")).asCentered()
            )

        private fun buildConnectionButton() = Button(
            i18n("google.books.dock.connection"),
            icon("google-icon")
        ).apply {
            setOnAction { showGoogleBookJoiner() }
        }

        fun showGoogleBookJoiner() {
            context.sendRequest(
                DatabaseView.TabItemShowRequest(
                    GoogleBookJoinTab(context, items[0]) { tab, record, volume ->
                        context.sendRequest(DatabaseView.TabItemCloseRequest(tab))
                        if (items[0] == record)
                            get(ExecutorService::class, "cachedExecutor").submit(buildJoinActionTask(volume))
                    }
                )
            )
        }

        private fun buildJoinActionTask(volume: Volume) =
            object : Task<Unit>() {

                init {
                    setOnRunning { context.showIndeterminateProgress() }
                    setOnFailed { event ->
                        //TODO: ERROR DIALOG
                        logger.error("Couldn't join with Google Book", event.source.exception)
                        context.stopProgress()
                    }
                    setOnSucceeded {
                        context.stopProgress()
                        refresh()
                        normalUI()
                        context.showInformationNotification(
                            i18n("google.books.dock.success_join.title"),
                            null,
                            Duration.seconds(2.0)
                        )
                    }
                }

                override fun call() {
                    items[0].serviceConnection?.googleBookHandle = volume.id
                    database.updateRecord(items[0])
                }
            }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBookConnectionView::class.java)
    }
}