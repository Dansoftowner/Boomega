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

package com.dansoftware.boomega.gui.dbcreator

import com.dansoftware.boomega.database.api.DatabaseConstructionException
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.database.api.RegistrationForm
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.asObjectProperty
import com.dansoftware.boomega.gui.util.not
import com.dansoftware.boomega.gui.util.padding
import com.dansoftware.boomega.i18n.i18n
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import java.util.concurrent.TimeUnit

class DatabaseCreatorForm(
    private val context: Context,
    private val databaseType: ObjectProperty<DatabaseProvider<*>>
) : BorderPane() {

    private val registrationFormCache: Cache<DatabaseProvider<*>, RegistrationForm<*>> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10)
            .build()

    private val registrationForm: ObjectProperty<RegistrationForm<*>?> =
        Bindings.createObjectBinding(
            {
                databaseType.get()?.let {
                    registrationFormCache.get(it) {
                        // TODO: database options
                        databaseType.get()?.buildUIRegistrationForm(context, emptyMap())
                    }
                }
            },
            databaseType
        ).asObjectProperty()

    var createdDatabase: DatabaseMeta? = null
        private set

    init {
        buildUI()
    }

    private fun buildUI() {
        center = buildCenter()
        bottom = buildBottom()
    }

    private fun buildCenter() = ScrollPane().apply {
        isFitToWidth = true
        contentProperty().bind(Bindings.createObjectBinding({ registrationForm.get()?.node }, registrationForm))
    }

    private fun buildBottom() = Button().run {
        maxWidth = Double.MAX_VALUE
        minHeight = 35.0
        text = i18n("database.creator.create")
        isDefaultButton = true
        registrationForm.addListener { _, _, form ->
            disableProperty().bind(form!!.persistable.not())
        }
        setOnAction {
            constructDatabase()?.let {
                get(DatabaseTracker::class).saveDatabase(it)
                createdDatabase = it
                context.close()
            }
        }
        StackPane(this).padding(Insets(10.0))
    }

    private fun constructDatabase() =
        try {
            registrationForm.get()!!.registrate()
        } catch (e: DatabaseConstructionException) {
            context.showErrorDialog(
                title = e.title ?: i18n("database.create_failed"),
                message = e.localizedMessage ?: "",
                cause = e
            )
            null
        }
}