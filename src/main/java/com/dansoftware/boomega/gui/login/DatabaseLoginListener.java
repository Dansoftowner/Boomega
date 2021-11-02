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

package com.dansoftware.boomega.gui.login;

import com.dansoftware.boomega.database.api.Database;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import org.jetbrains.annotations.NotNull;

/**
 * A database login listener is notified whenever a {@link LoginActivity}
 * authenticates a database.
 */
public interface DatabaseLoginListener {

    /**
     * Called when the login activity has successfully authenticated a database
     *
     * @param database the launched database
     */
    void onDatabaseOpened(@NotNull Database database);

    /**
     * Called when the login activity opens a database that is already in use.
     * This method is intended to handle these situations
     *
     * @param databaseMeta the database-meta
     */
    default void onUsedDatabaseOpened(@NotNull DatabaseMeta databaseMeta) { }
}
