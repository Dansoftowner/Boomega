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

package com.dansoftware.boomega.gui.imgviewer

import javafx.scene.image.Image
import javafx.stage.Window

/**
 * Used for showing an [ImageViewer] with an [ImageViewerWindow].
 *
 * @author Daniel Gyorffy
 */
class ImageViewerActivity(private val image: Image, private val owner: Window? = null) {
    fun show() = ImageViewerWindow(owner, ImageViewer(image)).show()
}