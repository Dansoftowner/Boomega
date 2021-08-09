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

package com.dansoftware.boomega.gui.export

import com.dansoftware.boomega.export.api.RecordExportConfiguration
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.gui.api.Context

/**
 * A [ConfigurationDialog] is provided by a particular [RecordExporter].
 * It has the ability to show a configuration-panel on the GUI that allows to
 * create a [RecordExportConfiguration].
 *
 * @param C the [RecordExportConfiguration] this dialog creates
 * @see RecordExporter.configurationDialog
 */
interface ConfigurationDialog<C : RecordExportConfiguration> {

    /**
     * Requests the dialog to be shown.
     *
     * @param context the [Context] that provides access to the UI
     * @param onFinished the callback that will receive the configuration
     */
    fun show(context: Context, onFinished: (C) -> Unit)
}