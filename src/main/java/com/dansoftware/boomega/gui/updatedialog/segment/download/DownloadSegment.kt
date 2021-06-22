package com.dansoftware.boomega.gui.updatedialog.segment.download

import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.update.UpdateInformation
import com.dansoftware.sgmdialog.TitledSegment
import javafx.fxml.FXMLLoader
import javafx.scene.Node

/**
 * Allows the user to download the new app from the internet in a [com.dansoftware.boomega.gui.updatedialog.UpdateDialog].
 * The user can select what type of file should be downloaded.
 *
 * @author Daniel Gyorffy
 */
class DownloadSegment(
    private val context: Context,
    private val updateInformation: UpdateInformation
) : TitledSegment(
    I18N.getValue("update.segment.download.name"),
    I18N.getValue("update.segment.download.title")
) {
    override fun getCenterContent(): Node =
        FXMLLoader(javaClass.getResource("DownloadSegment.fxml"), I18N.getValues(), null) {
            DownloadSegmentController(context, updateInformation)
        }.load()
}