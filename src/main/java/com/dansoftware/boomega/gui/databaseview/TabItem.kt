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

package com.dansoftware.boomega.gui.databaseview

import javafx.scene.Node

/**
 * A basic representation of a tab in the [TabView].
 */
class TabItem(val id: String, val title: String, val graphicFactory: () -> Node?, val contentFactory: () -> Node) {
    var onCloseRequest: (() -> Boolean)? = null

    constructor(
        id: String,
        title: String,
        graphicFactory: () -> Node?,
        contentFactory: () -> Node,
        onCloseRequest: () -> Boolean
    ) : this(id, title, graphicFactory, contentFactory) {
        this.onCloseRequest = onCloseRequest
    }
}