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
import com.dansoftware.boomega.gui.control.WebsiteHyperLink
import com.dansoftware.boomega.gui.updatedialog.UpdateDialog
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.update.Release
import com.dansoftware.boomega.update.ReleaseAsset
import com.dansoftware.boomega.update.sizeInMegaBytes
import com.dansoftware.boomega.util.revealInExplorer
import com.jfilegoodies.FileGoodies
import com.juserdirs.UserDirectories
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.stage.DirectoryChooser
import org.controlsfx.control.textfield.CustomTextField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import kotlin.properties.Delegates

class DownloadView(private val context: Context, private val release: Release) : VBox(10.0) {

    private val selectedAsset = SimpleObjectProperty<ReleaseAsset>()
    private val downloadDirectory = SimpleStringProperty()
    private val downloadService = DownloadService(context, release, selectedAsset, downloadDirectory)
    private val dialog: UpdateDialog
        get() = parent.parent as UpdateDialog

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.add(Label(i18n("update.dialog.download.select_format")))
        children.add(buildAssetChooser())
        children.add(Separator())
        children.add(Label(i18n("update.dialog.download.path.prompt")))
        children.add(buildPathField())
        children.add(buildDownloadArea())
        children.add(buildProgressbar())
        children.add(buildProgressDetailArea())
        children.add(buildInstallButton())
        children.add(buildOpenButton())
        children.add(buildWebsiteHyperlink())
    }

    private fun buildAssetChooser() = AssetChooser(release.assets!!).apply {
        this@DownloadView.selectedAsset.bind(selectedAsset)
    }

    private fun buildPathField() = CustomTextField().also { textField ->
        downloadDirectory.bind(textField.textProperty())
        textField.disableProperty().bind(downloadService.runningProperty())
        textField.text = UserDirectories.get().downloadsDirectoryPath()
        textField.isEditable = false
        textField.right = StackPane(icon("folder-open-icon").apply {
            cursor = Cursor.HAND
            setOnMouseClicked {
                textField.text = DirectoryChooser().apply { initialDirectory = textField.text?.let(::File) }
                    .showDialog(context.contextWindow)
                    ?.absolutePath
                    ?: textField.text
            }
        }).padding(Insets(5.0))
    }

    private fun buildDownloadArea() =
        HBox(5.0, buildDownloadButton(), buildPauseButton(), buildSuspendButton())

    private fun buildDownloadButton() = Button().apply {
        hgrow(Priority.ALWAYS)
        text = i18n("update.dialog.download_binary")
        maxWidth = Double.MAX_VALUE
        disableProperty().bind(
            downloadService.runningProperty()
                .or(downloadDirectory.isNull)
                .or(selectedAsset.isNull)
                .or(downloadService.valueProperty().isNotNull)
        )
        setOnAction {
            initUpdateDialogBehaviour()
            downloadService.reset()
            downloadService.start()
        }
    }

    private fun buildPauseButton() = Button().apply {
        disableProperty().bind(downloadService.runningProperty().not())
        graphicProperty().bind(
            Bindings.createObjectBinding(
                { icon(if (downloadService.isPaused) "play-icon" else "pause-icon") },
                downloadService.pausedProperty, downloadService.runningProperty()
            )
        )
        setOnAction {
            downloadService.pauseOrResume()
        }
    }

    private fun buildSuspendButton() = Button().apply {
        disableProperty().bind(downloadService.runningProperty().not())
        graphic = icon("stop-icon")
        setOnAction {
            downloadService.cancel()
        }
    }

    private fun buildProgressbar() = ProgressBar().apply {
        bindFullVisibilityTo(downloadService.runningProperty())
        progressProperty().bind(downloadService.progressProperty())
        downloadService.pausedProperty.addListener { _, _, isPaused ->
            pseudoClassStateChanged(PseudoClass.getPseudoClass("paused"), isPaused)
        }
    }

    private fun buildProgressDetailArea() = HBox(2.0).apply {
        children.add(buildProgressMessageLabel())
        children.add(buildProgressTitleLabel())
    }

    private fun buildProgressTitleLabel() = Label().apply {
        hgrow(Priority.ALWAYS)
        bindFullVisibilityTo(downloadService.runningProperty())
        textProperty().bind(downloadService.titleProperty())
    }

    private fun buildProgressMessageLabel() = Label().apply {
        bindFullVisibilityTo(downloadService.runningProperty())
        hgrow(Priority.ALWAYS)
        textProperty().bind(downloadService.messageProperty())
        textAlignment = TextAlignment.RIGHT
    }

    private fun buildInstallButton() = Button().apply {
        bindFullVisibilityTo(downloadService.valueProperty().isNotNull)
        text = i18n("update.dialog.download.install")
        maxWidth = Double.MAX_VALUE
        isDefaultButton = true
        setOnAction {
            downloadService.value?.let {
                it.takeIf(FileGoodies::isOSExecutable)?.let {
                    try {
                        Runtime.getRuntime().exec(it.absoluteFile.absolutePath)
                    } catch (e: IOException) {
                        context.showErrorDialog(
                            I18N.getValue("update.dialog.download.install.failed.title"),
                            I18N.getValue("update.dialog.download.install.failed.msg", it.name),
                            e
                        ) {}
                    }
                } ?: it.revealInExplorer()
            }
        }
    }

    private fun buildOpenButton() = Button().apply {
        bindFullVisibilityTo(downloadService.valueProperty().isNotNull)
        text = i18n("update.dialog.download.open_in_explorer")
        maxWidth = Double.MAX_VALUE
        setOnAction {
            downloadService.value?.revealInExplorer()
        }
    }

    private fun buildWebsiteHyperlink() =
        WebsiteHyperLink(i18n("website.open"), release.website).asCentered()

    private fun initUpdateDialogBehaviour() {
        dialog.prevButtonDisableProperty().bind(downloadService.runningProperty())
        dialog.nextButtonDisableProperty().bind(downloadService.runningProperty())
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DownloadView::class.java)
    }

    private class DownloadService(
        private val context: Context,
        private val release: Release,
        private val releaseAsset: ObjectProperty<ReleaseAsset>,
        private val downloadDirectory: StringProperty
    ) : Service<File>() {

        val pausedProperty = SimpleBooleanProperty()
        val isPaused get() = pausedProperty.get()

        private var task: DownloaderTask? by Delegates.observable(null) { _, _, it ->
            it?.let { pausedProperty.bind(it.pausedProperty().and(runningProperty())) }
        }

        init {
            onFailed(::onFailed)
            onCancelled(::onCancelled)
            onSucceeded { onSucceeded() }
            progressProperty().addListener { _, _, _ -> onProgress() }
        }

        override fun createTask(): Task<File> =
            DownloaderTask(release, releaseAsset.get(), File(downloadDirectory.get())).also(::task::set)

        fun pauseOrResume() {
            task!!.isPaused = task!!.isPaused.not()
        }

        private fun onFailed(exception: Throwable) {
            logger.error("Failed during update downloading", exception)

            context.showProgress(
                workDone.toLong(),
                totalWork.toLong(),
                Context.ProgressType.ERROR
            )

            context.showErrorDialog(
                I18N.getValue("update.dialog.download.failed.title"),
                I18N.getValue("update.dialog.download.failed.msg"),
                exception as? Exception
            ) { context.stopProgress() }
        }

        private fun onSucceeded() {
            context.stopProgress()
            context.contextWindow?.requestFocus()
        }

        private fun onCancelled() {
            context.stopProgress()
        }

        private fun onProgress() {
            context.showProgress(
                workDone.toLong(),
                totalWork.toLong(),
                when {
                    isPaused -> Context.ProgressType.PAUSED
                    else -> Context.ProgressType.NORMAL
                }
            )
        }
    }

    private class AssetChooser(private val assets: List<ReleaseAsset>) : VBox(10.0) {

        private val radioGroup = ToggleGroup()
        val selectedAsset = Bindings.createObjectBinding<ReleaseAsset>(
            { radioGroup.selectedAsset },
            radioGroup.selectedToggleProperty()
        )

        init {
            children.add(buildScrollPane())
            children.add(buildSizeIndicatorLabel())
        }

        private fun buildScrollPane() = object : ScrollPane() {
            init {
                padding = Insets(5.0)
                prefHeight = 120.0
                isFitToWidth = true
                content = buildRadioBoxes().takeIf(List<*>::isNotEmpty)?.let {
                    VBox(5.0).apply { children.addAll(it) }
                } ?: NoBinaryAvailablePlaceHolder()
            }

            private fun buildRadioBoxes() =
                assets.sortedBy(ReleaseAsset::name).map {
                    RadioButton(it.name).apply {
                        properties["github.asset"] = it
                        toggleGroup = radioGroup
                    }
                }
        }

        private fun buildSizeIndicatorLabel() = Label().apply {
            textProperty().bind(
                SimpleStringProperty(i18n("update.dialog.download.size"))
                    .concat(" ")
                    .concat(
                        Bindings.createStringBinding({
                            radioGroup.selectedAsset?.sizeInMegaBytes?.toString()?.plus(" MB") ?: "-"
                        }, radioGroup.selectedToggleProperty())
                    )
            )
        }

        private val Toggle.asset get() = properties["github.asset"] as? ReleaseAsset
        private val ToggleGroup.selectedAsset get() = selectedToggle?.asset
    }

}