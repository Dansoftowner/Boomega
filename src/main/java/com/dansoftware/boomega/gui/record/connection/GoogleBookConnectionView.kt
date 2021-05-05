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

package com.dansoftware.boomega.gui.record.connection

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQueryBuilder
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.googlebooks.details.GoogleBookDetailsPane
import com.dansoftware.boomega.gui.googlebooks.join.GoogleBookJoinerOverlay
import com.dansoftware.boomega.gui.record.RecordTable
import com.dansoftware.boomega.gui.util.I18NButtonTypes
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import jfxtras.styles.jmetro.JMetroStyleClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val detailsPane: GoogleBookDetailsPane = GoogleBookDetailsPane(context)
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
    }

    private fun initListObserver(selectedItems: ObservableList<Record>) {
        selectedItems.addListener(ListChangeListener { items = selectedItems })
    }

    fun removeConnectionRequest() {
        context.showDialog(
            I18N.getValue("google.books.dock.remove.confirmation.title", items.size),
            buildPreviewTable(items),
            {
                if (it.typeEquals(ButtonType.YES)) {
                    CachedExecutor.submit(ConnectionRemoveTask())
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
        noSelectionUI()
    }

    private fun buildCache(): Cache<String, Volume> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    private fun handleNewItems(items: List<Record>) {
        retrieveGoogleBookHandle(items)?.let {
            normalUI()
            pullVolume(it)
        } ?: when {
            items.isEmpty() -> noSelectionUI()
            items.size == 1 -> noConnectionUI()
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

    private fun noSelectionUI() {
        vBox.children.setAll(NoSelectionPlaceHolder())
    }

    private fun multipleSelectionUI() {
        vBox.children.setAll(MultipleSelectionPlaceHolder())
    }

    private fun errorUI() {
        vBox.children.setAll(ErrorPlaceHolder())
    }

    private fun buildPreviewTable(records: List<Record>) =
        RecordTable(0).apply {
            addColumn(RecordTable.ColumnType.INDEX_COLUMN)
            addColumn(RecordTable.ColumnType.TYPE_INDICATOR_COLUMN)
            addColumn(RecordTable.ColumnType.AUTHOR_COLUMN)
            addColumn(RecordTable.ColumnType.TITLE_COLUMN)
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
                errorUI()
            }
        }

        override fun call(): Volume {
            logger.debug("Starting pull task...")
            return GoogleBooksQueryBuilder.byId(googleHandle).load()
        }

        fun start() {
            cacheExisting()
            volumeCache.getIfPresent(googleHandle)?.let {
                logger.debug("Found cache for: '{}'", googleHandle)
                currentGoogleHandle = googleHandle
                currentVolume = it
            } ?: CachedExecutor.submit(this)
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
                    I18N.getValue("google.books.dock.success_unjoin.title"),
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

    private class NoSelectionPlaceHolder() : StackPane() {
        init {
            buildUI()
        }

        private fun buildUI() {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            children.add(buildLabel())
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        private fun buildLabel() =
            Label(I18N.getValue("google.books.dock.placeholder.noselection")).apply {
                styleClass.add("place-holder-label")
            }
    }

    private class MultipleSelectionPlaceHolder : StackPane() {
        init {
            buildUI()
        }

        private fun buildUI() {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            children.add(buildLabel())
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        private fun buildLabel() =
            Label(I18N.getValue("google.books.dock.placeholder.multiple")).apply {
                styleClass.add("place-holder-label")
                minHeight = 0.0
            }
    }

    private inner class ErrorPlaceHolder : StackPane() {
        init {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            VBox.setVgrow(this, Priority.ALWAYS)
            buildUI()
        }

        private fun buildUI() {
            children.add(
                Group(
                    VBox(
                        20.0,
                        StackPane(buildLabel()),
                        StackPane(buildDetailsButton())
                    )
                )
            )
        }

        private fun buildLabel() =
            Label(I18N.getValue("google.books.dock.placeholder.error")).apply {
                styleClass.add("place-holder-label")
            }

        private fun buildDetailsButton() =
            Button(
                I18N.getValue("google.books.dock.placeholder.error.details"),
                MaterialDesignIconView(MaterialDesignIcon.DETAILS)
            ).apply {
                setOnAction {
                    context.showErrorDialog(
                        "",
                        "",
                        it.source as Exception
                    )
                }
            }
    }

    private inner class NoConnectionPlaceHolder() : StackPane() {

       init {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            VBox.setVgrow(this, Priority.ALWAYS)
            buildUI()
        }

        private fun buildUI() {
            children.add(buildContent())
        }

        private fun buildContent(): Node {
            return Group(
                VBox(
                    20.0,
                    StackPane(buildLabel()),
                    buildConnectionButton()
                )
            )
        }

        private fun buildLabel() =
            Label(I18N.getValue("google.books.dock.placeholder.noconn")).apply {
                styleClass.add("place-holder-label")
            }

        private fun buildConnectionButton() = Button(
            I18N.getValue("google.books.dock.connection"),
            MaterialDesignIconView(MaterialDesignIcon.GOOGLE)
        ).apply {
            setOnAction { showGoogleBookJoiner() }
        }

        fun showGoogleBookJoiner() {
            context.showOverlay(
                GoogleBookJoinerOverlay(context) { volume ->
                    CachedExecutor.submit(
                        buildJoinActionTask(volume)
                    )
                }
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
                            I18N.getValue("google.books.dock.success_join.title"),
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

        init {
            buildUI()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBookConnectionView::class.java)
    }
}