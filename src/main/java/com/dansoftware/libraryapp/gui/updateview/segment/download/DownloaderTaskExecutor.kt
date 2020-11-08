package com.dansoftware.libraryapp.gui.updateview.segment.download

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.update.DownloadableBinary
import com.dansoftware.libraryapp.update.UpdateInformation
import com.nativejavafx.taskbar.TaskbarProgressbar
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import java.io.File

/**
 * Used for creating a [DownloaderTask] easily. Utility class for [DownloadSegmentController].
 *
 * @author Daniel Gyorffy
 */
class DownloaderTaskInitializer(val context: Context,
                                        val updateInformation: UpdateInformation,
                                        val controller: DownloadSegmentController) {

    /**
     * Constructs the [DownloaderTask] object
     *
     * @param binary the object that represents the downloadable binary
     * @param downloadDirectory the directory
     */
    fun construct(binary: DownloadableBinary, downloadDirectory: File): DownloaderTask {
        val downloaderTask = DownloaderTask(updateInformation, binary, downloadDirectory)
        downloaderTask.setOnRunning {
            controller.progressBar.progressProperty().unbind()
            controller.progressBar.progressProperty().bind(downloaderTask.progressProperty())

            controller.downloadPauseBtn.disableProperty().unbind()
            controller.downloadPauseBtn.disableProperty().bind(downloaderTask.runningProperty().not())

            controller.downloadKillBtn.disableProperty().unbind()
            controller.downloadKillBtn.disableProperty().bind(downloaderTask.runningProperty().not())

            controller.downloadBtn.disableProperty().unbind()
            controller.downloadBtn.disableProperty().bind(downloaderTask.runningProperty())

            controller.fileOpenerBtn.disableProperty().unbind()
            controller.fileOpenerBtn.disableProperty().bind(downloaderTask.workDoneProperty().lessThan(100))

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
            controller.taskbarProgressbar.stopProgress()
            clearProgress()
        }

        downloaderTask.setOnFailed { event ->
            controller.taskbarProgressbar.showCustomProgress(
                    downloaderTask.workDone.toLong(),
                    downloaderTask.totalWork.toLong(),
                    TaskbarProgressbar.Type.ERROR
            )
            val cause = event.source.exception

            context.showErrorDialog(
                    I18N.getAlertMsg("update.view.download.failed.title"),
                    I18N.getAlertMsg("update.view.download.failed.msg"),
                    cause as Exception) { controller.taskbarProgressbar.stopProgress() }
            clearProgress()
        }

        downloaderTask.setOnSucceeded {
            controller.taskbarProgressbar.stopProgress()
            context.contextWindow.requestFocus()
            clearProgress()
        }

        return downloaderTask
    }

    private fun clearProgress() {
        controller.progressBar.progressProperty().unbind()
        controller.progressBar.progress = 0.0
        controller.root.children.removeAt(6)
    }
}