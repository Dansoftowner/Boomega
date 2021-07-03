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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.Module
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.model.WorkbenchModule
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

/**
 * The [GoogleBooksImportModule] is a module that allows to search books
 * through the Google Books service and also allows to import books and add them into
 * the local database.
 *
 * @author Daniel Gyorffy
 */
class GoogleBooksImportModule(
    private val context: Context,
    private val preferences: Preferences
) : Module {

    override val name: String
        get() = I18N.getValue("google.books.import.module.title")
    override val icon: Node
        get() = MaterialDesignIconView(MaterialDesignIcon.GOOGLE)
    override val id: String
        get() = "google-books-import"

    private val content: ObjectProperty<GoogleBooksImportView> = SimpleObjectProperty()

    override fun activate(): Node {
        if (content.get() == null)
            content.set(GoogleBooksImportView(context, preferences))
        return content.get()
    }

    override fun close(): Boolean {
        content.get().writeConfig()
        content.set(null)
        return true
    }
}