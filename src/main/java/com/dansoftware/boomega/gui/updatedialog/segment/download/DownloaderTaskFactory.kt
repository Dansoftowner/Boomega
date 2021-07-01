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

package com.dansoftware.boomega.gui.updatedialog.segment.download

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.update.DownloadableBinary
import com.dansoftware.boomega.update.UpdateInformation
import javafx.beans.property.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DownloaderTaskFactory(
    private val context: Context,
    private val updateInformation: UpdateInformation
) {

    private val downloaderTask: ObjectProperty<DownloaderTask> = SimpleObjectProperty()

    var isPaused: Boolean
        get() = downloaderTask.get()?.isPaused ?: false
        set(value) {
            downloaderTask.get()?.isPaused = value
        }

    val pausedProperty: BooleanProperty = SimpleBooleanProperty().apply {
        onNewTaskCreated {
            bind(it?.pausedProperty())
        }
    }

    val progressProperty: DoubleProperty = SimpleDoubleProperty().apply {
        onNewTaskCreated {
            bind(it?.progressProperty())
        }
    }

    val runningProperty: BooleanProperty = SimpleBooleanProperty().apply {
        onNewTaskCreated {
            bind(it?.runningProperty())
        }
    }

    val workDoneProperty: DoubleProperty = SimpleDoubleProperty().apply {
        onNewTaskCreated {
            bind(it?.workDoneProperty())
        }
    }

    val titleProperty: StringProperty = SimpleStringProperty().apply {
        onNewTaskCreated {
            bind(it?.titleProperty())
        }
    }

    val messageProperty: StringProperty = SimpleStringProperty().apply {
        onNewTaskCreated {
            bind(it?.messageProperty())
        }
    }

    val file: File?
        get() = downloaderTask.get()?.value

    fun onNewTaskCreated(action: (DownloaderTask?) -> Unit) {
        downloaderTask.addListener { _, _, newTask ->
            action(newTask)
        }
    }

    fun buildTask(binary: DownloadableBinary, dir: File) =
        DownloaderTask(updateInformation, binary, dir).apply {
            initialize(this)
            downloaderTask.set(this)
        }

    fun start(binary: DownloadableBinary, dir: File) {
        DownloadingThread(buildTask(binary, dir)).start()
    }

    fun startPause() {
        isPaused = isPaused.not()
    }

    fun kill() {
        downloaderTask.get()?.cancel()
    }

    private fun initialize(task: DownloaderTask) {
        task.apply {
            setOnCancelled { context.stopProgress() }
            setOnFailed { onFailed(this, it.source.exception) }
            setOnSucceeded { onSucceeded() }
            progressProperty().addListener { _, _, _ ->
                onProgress(task.workDone, task.totalWork, task.isPaused)
            }
        }
    }

    private fun onFailed(task: DownloaderTask, exception: Throwable) {
        context.showProgress(
            task.workDone.toLong(),
            task.totalWork.toLong(),
            Context.ProgressType.ERROR
        )

        logger.error("DownloaderTask failed with an exception: {}", exception)

        context.showErrorDialog(
            I18N.getValue("update.dialog.download.failed.title"),
            I18N.getValue("update.dialog.download.failed.msg"),
            exception as Exception
        ) { context.stopProgress() }
    }

    private fun onSucceeded() {
        context.stopProgress()
        context.contextWindow?.requestFocus()
    }

    private fun onProgress(workDone: Number, totalWork: Number, isPaused: Boolean) {
        context.showProgress(
            workDone.toLong(),
            totalWork.toLong(),
            when {
                isPaused -> Context.ProgressType.PAUSED
                else -> Context.ProgressType.NORMAL
            }
        )
    }

    private class DownloadingThread(task: DownloaderTask) : Thread(task) {
        init {
            isDaemon = true
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DownloaderTaskFactory::class.java)
    }
}