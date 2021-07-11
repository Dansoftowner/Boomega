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

package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.gui.api.Context
import javafx.scene.Node
import javafx.scene.layout.StackPane

class GoogleBookSearchView(private val context: Context) : StackPane() {

    private var content: Node?
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    init {
        styleClass.add("google-books-search-view")
        buildUI()
    }

    private fun buildUI() {
        content = GoogleBookSearchForm(context) {

        }
    }
}