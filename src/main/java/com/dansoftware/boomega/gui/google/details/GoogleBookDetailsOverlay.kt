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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.base.TitledOverlayBox
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.scene.image.ImageView

/**
 * Used for displaying a [GoogleBookDetailsPane] as an overlay
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailsOverlay(context: Context, volume: Volume) : TitledOverlayBox(
    i18n("google.books.detail.title"),
    ImageView("/com/dansoftware/boomega/image/util/google_12px.png"),
    GoogleBookDetailsPane(context, volume).apply { minHeight = 300.0 }
)