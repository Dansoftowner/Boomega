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
import com.dansoftware.boomega.update.Release
import com.dansoftware.sgmdialog.TitledSegment
import javafx.scene.Node

/**
 * Allows the user to download the new app from the internet in a [com.dansoftware.boomega.gui.updatedialog.UpdateDialog].
 * The user can select what type of file should be downloaded.
 *
 * @author Daniel Gyorffy
 */
class DownloadSegment(
    private val context: Context,
    private val release: Release
) : TitledSegment(
    I18N.getValue("update.segment.download.name"),
    I18N.getValue("update.segment.download.title")
) {
    override fun getCenterContent(): Node = DownloadView(context, release)
}