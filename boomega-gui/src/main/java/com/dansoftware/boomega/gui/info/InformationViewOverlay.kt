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

package com.dansoftware.boomega.gui.info

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.base.TitledOverlayBox
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Tooltip
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

class InformationViewOverlay(context: Context) :
    TitledOverlayBox(
        i18n("info.view.title"),
        icon("info-icon"),
        InformationView(context),
        resizableH = true,
        resizableV = false,
        Button().apply {
            padding = Insets(0.0)
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = icon("copy-icon")
            tooltip = Tooltip(i18n("info.copy"))
            setOnAction {
                ClipboardContent().also { clipboardContent ->
                    clipboardContent.putString(getApplicationInfoCopy())
                    Clipboard.getSystemClipboard().setContent(clipboardContent)
                }
            }
        }
    )