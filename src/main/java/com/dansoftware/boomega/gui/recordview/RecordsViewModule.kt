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

package com.dansoftware.boomega.gui.recordview

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.Module
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RecordsViewModule(
    private val context: Context,
    private val preferences: Preferences,
    private val database: Database
) : Module() {

    override val name: String
        get() = I18N.getValue("record.book.view.module.name")
    override val icon: Node
        get() = icon("library-icon")
    override val id: String
        get() = "records-view-module"

    private val content: ObjectProperty<RecordsView> = SimpleObjectProperty()

    override fun buildContent(): Node =
        content.get() ?: RecordsView(context, database, preferences).also(content::set)

    override fun destroy(): Boolean = true.also {
        logger.debug("Module closed. Writing configurations...")
        //content.get()?.writeConfig()
        content.set(null)
    }

    override fun sendMessage(message: Message) {
        content.get()?.let { view ->
            when (message) {
                is InsertionRequest ->
                    view.insertNewRecord(message.record)
            }
        }
    }

    class InsertionRequest(val record: Record) : Message

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordsViewModule::class.java)
    }
}