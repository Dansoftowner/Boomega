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

package com.dansoftware.boomega.db.api

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.gui.api.Context
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import javafx.scene.layout.Region

/**
 * A flexible abstraction of a particular database-system.
 */
interface DatabaseProvider {

    /**
     * Gives the name of the Database system (e.g. "MySql" or "MongoDb")
     */
    val name: String

    /**
     * Gives the database's icon
     */
    val icon: Node

    /**
     * Gives the list of the configurable options can be used for the database
     */
    val availableOptions: List<DatabaseOption>

    /**
     * Gives the list of fields can be used for
     * specifying credentials or other required/optional values
     * for authenticating/creating the particular database
     */
    val fields: List<DatabaseField>

    /**
     * Gives a database meta information object from the given "url" or identifier
     */
    fun getMeta(url: String): DatabaseMeta

    /**
     * Constructs the actual [Database] object that allows to access the database
     *
     * @param credentials the credentials specified for [DatabaseField]s
     * @param options the initial database options
     */
    fun getDatabase(
        credentials: Map<DatabaseField, Any?>,
        options: List<DatabaseOption>
    ): Database

    /**
     * Builds a basic UI login form
     *
     * @param context
     * @param databaseMeta the database-meta object-property;
     *                     the login form will try to authenticate
     *                     the database based on it.
     * @param options the initial database options
     * @param onDatabaseAuthenticated the action to perform when the user successfully logged in
     */
    fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<DatabaseMeta>,
        options: List<DatabaseOption>,
        onDatabaseAuthenticated: (Database) -> Unit
    ): Region

    /**
     * Builds a basic UI registration form
     *
     * @param context
     * @param options the initial database options
     * @param onDatabaseCreated the action to perform when the user successfully completed the registration
     */
    fun buildUIRegistrationForm(
        context: Context,
        options: List<DatabaseOption>,
        onDatabaseCreated: (DatabaseMeta) -> Unit
    )
}