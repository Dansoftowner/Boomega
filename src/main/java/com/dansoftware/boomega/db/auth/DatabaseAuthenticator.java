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

package com.dansoftware.boomega.db.auth;

import com.dansoftware.boomega.db.Credentials;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A {@link DatabaseAuthenticator} is used for authenticating and creating/opening databases.
 *
 * <p>
 * A {@link DatabaseAuthenticator} itself is an abstract structure; each database-type requires a different implementation.
 * If exception handling is needed, a {@link FailListener} can be used.
 *
 * <p>
 * <pre>{@code
 *
 * }</pre>
 *
 * @author Daniel Gyorffy
 * @see FailListener
 */
public abstract class DatabaseAuthenticator {

    private FailListener onFailed;

    public DatabaseAuthenticator() {
    }

    public DatabaseAuthenticator onFailed(@Nullable FailListener onFailed) {
        this.onFailed = onFailed;
        return this;
    }

    /**
     * Creates/Opens the particular database that doesn't need credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @return the {@link Database} object
     */
    @Nullable
    public Database auth(@NotNull DatabaseMeta databaseMeta) {
        return this.auth(databaseMeta, Credentials.anonymous());
    }

    /**
     * Creates/Opens the particular database.
     *
     * <p>
     * If the database is not created yet, a database will be created with the given credentials.
     * Otherwise it will try to open the database with the credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @param credentials  the object that holds the credentials
     * @return the {@link Database} object.
     */
    @Nullable
    public Database auth(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) {
        return create(databaseMeta, credentials, getOnFailListener().orElseGet(FailListener::empty));
    }

    /**
     * Creates the particular database if not exists yet.
     *
     * @param databaseMeta the meta-information of the database
     * @return {@code true} if the database is created successfully; {@code false} otherwise.
     */
    public boolean touch(@NotNull DatabaseMeta databaseMeta) {
        return this.touch(databaseMeta, Credentials.anonymous());
    }

    /**
     * Creates the particular database if not exists yet with credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @param credentials  the object that holds the credentials
     * @return {@code true} if the database is created successfully; {@code false} otherwise.
     */
    public boolean touch(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) {
        Database created = auth(databaseMeta, credentials);
        if (created != null) {
            created.close();
            return true;
        }

        return false;
    }

    public Optional<FailListener> getOnFailListener() {
        return Optional.ofNullable(this.onFailed);
    }

    /**
     * Performs the database creating operation.
     *
     * <p>
     * All {@link DatabaseAuthenticator} implementations must implement it.
     *
     * @param databaseMeta the meta-information about the database
     * @param credentials  the database-credentials
     * @param failListener the FailListener that defines how to handle exceptions
     * @return the created "database-bridge"
     */
    protected abstract Database create(@NotNull DatabaseMeta databaseMeta,
                                       @NotNull Credentials credentials,
                                       @NotNull FailListener failListener);
}
