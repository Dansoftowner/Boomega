package com.dansoftware.libraryapp.gui.updatedialog.segment.download

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.update.DownloadableBinary
import com.dansoftware.libraryapp.update.UpdateInformation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Used for creating a [DownloaderTask] easily. Utility class for [DownloadSegmentController].
 *
 * @author Daniel Gyorffy
 */
class DownloaderTaskExecutor(val context: Context,
                             val updateInformation: UpdateInformation,
                             val controller: DownloadSegmentController) {

    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(DownloaderTaskExecutor::class.java)
    }

    private var downloaderTask: DownloaderTask? = null

    /**
     * Constructs the [DownloaderTask] object
     *
     * @param binary the object that represents the downloadable binary
     * @param downloadDirectory the directory
     */
    fun start(binary: DownloadableBinary, downloadDirectory: File) {
        when {
            downloaderTask === null -> startNewTask(DownloaderTask(this.updateInformation, binary, downloadDirectory))
            downloaderTask!!.isDone -> startNewTask(DownloaderTask(this.updateInformation, binary, downloadDirectory))
        }
    }

    fun kill() {
        if (downloaderTask !== null) {
            downloaderTask!!.isPaused = true
            downloaderTask!!.cancel()
        }
    }

    fun startPause() = setPaused(!isPaused())

    private fun startNewTask(task: DownloaderTask) {
        this.downloaderTask = initialize(task)
        DownloadingThread(downloaderTask!!).start()
    }

    fun getResult(): File? =
            if (this.downloaderTask === null || this.downloaderTask!!.isRunning) null
            else downloaderTask!!.value

    fun isDone(): Boolean = downloaderTask?.isDone ?: false

    fun isRunning(): Boolean = downloaderTask?.isRunning ?: false

    fun isPaused(): Boolean = downloaderTask?.isPaused ?: false

    fun setPaused(value: Boolean) {
        downloaderTask?.isPaused = value
    }

    private fun initialize(downloaderTask: DownloaderTask): DownloaderTask {
        downloaderTask.setOnRunning {
            downloaderTask.pausedProperty().addListener { _, _, paused ->
                when (paused) {
                    true -> controller.progressBar.styleClass.add("paused")
                    false -> controller.progressBar.styleClass.remove("paused")
                }
            }

            controller.progressBar.progressProperty().unbind()
            controller.progressBar.progressProperty().bind(downloaderTask.progressProperty())

            controller.downloadPauseBtn.disableProperty().unbind()
            controller.downloadPauseBtn.disableProperty().bind(downloaderTask.runningProperty().not())

            controller.downloadKillBtn.disableProperty().unbind()
            controller.downloadKillBtn.disableProperty().bind(downloaderTask.runningProperty().not())

            controller.downloadBtn.disableProperty().unbind()
            controller.downloadBtn.disableProperty().bind(downloaderTask.runningProperty())

            controller.downloadPathField.disableProperty().unbind()
            controller.downloadPathField.disableProperty().bind(downloaderTask.runningProperty())

            controller.downloadPathChooserBtn.disableProperty().unbind()
            controller.downloadPathChooserBtn.disableProperty().bind(downloaderTask.runningProperty())

            controller.fileOpenerBtn.disableProperty().unbind()
            controller.fileOpenerBtn.disableProperty().bind(downloaderTask.workDoneProperty().lessThan(100))

            controller.radioBtnVBox.disableProperty().unbind()
            controller.radioBtnVBox.disableProperty().bind(downloaderTask.runningProperty())

            controller.runnerBtn.disableProperty().unbind()
            controller.runnerBtn.disableProperty().bind(downloaderTask.workDoneProperty().lessThan(100))

            controller.dialog.nextButtonDisableProperty().bind(downloaderTask.runningProperty())
            controller.dialog.prevButtonDisableProperty().bind(downloaderTask.runningProperty())


            //creating a node that has two labels: one on the left, one on the right
            //the label on the left displays the actual message, the label on the right displays
            //the actual progress
            val progressIndicatorNode: Node = StackPane(object : Label() {
                init {
                    textProperty().bind(downloaderTask.titleProperty())
                    StackPane.setAlignment(this, Pos.CENTER_RIGHT)
                }
            }, object : Label() {
                init {
                    textProperty().bind(downloaderTask.messageProperty())
                    StackPane.setAlignment(this, Pos.CENTER_LEFT)
                }
            })

            controller.root.children.add(6, progressIndicatorNode)
        }

        downloaderTask.setOnCancelled {
            context.stopProgress()
            clearProgress()
        }

        downloaderTask.setOnFailed { event ->
            context.showProgress(
                    downloaderTask.workDone.toLong(),
                    downloaderTask.totalWork.toLong(),
                    Context.ProgressType.ERROR
            )
            val cause = event.source.exception

            logger.error("DownloaderTask failed with an exception: {}", cause)

            context.showErrorDialog(
                    I18N.getUpdateDialogValue("update.view.download.failed.title"),
                    I18N.getUpdateDialogValue("update.view.download.failed.msg"),
                    cause as Exception) { context.stopProgress() }
            clearProgress()
        }

        downloaderTask.setOnSucceeded {
            context.stopProgress()
            context.contextWindow?.requestFocus()
            clearProgress()
        }

        downloaderTask.progressProperty().addListener { _, _, _ ->
            context.showProgress(
                    downloaderTask.workDone.toLong(),
                    downloaderTask.totalWork.toLong(),
                    when {
                        isPaused() -> Context.ProgressType.PAUSED
                        else -> Context.ProgressType.NORMAL
                    }
            )
        }

        return downloaderTask
    }

    private fun clearProgress() {
        controller.progressBar.progressProperty().unbind()
        controller.progressBar.progress = 0.0
        controller.root.children.removeAt(6)
    }

    private class DownloadingThread(task: DownloaderTask) : Thread(task) {
        init {
            isDaemon = true
        }
    }
}