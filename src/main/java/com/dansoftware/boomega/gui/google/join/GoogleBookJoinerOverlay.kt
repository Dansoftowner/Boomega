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

package com.dansoftware.boomega.gui.google.join

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.base.TitledOverlayBox
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import java.util.function.Consumer

class GoogleBookJoinerOverlay(
    context: Context,
    onVolumeSelected: Consumer<Volume>
) : TitledOverlayBox(
    I18N.getValue("google.books.joiner.titlebar"),
    ImageView(Image("/com/dansoftware/boomega/image/util/google_12px.png")),
    GoogleBookJoinerView(context) { context.hideOverlay(it.parent?.parent?.parent?.parent as Region?) } //TODO: not so good solution
        .apply { setOnVolumeSelected(onVolumeSelected) }
)