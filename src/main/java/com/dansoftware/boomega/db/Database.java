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

package com.dansoftware.boomega.db;

import com.dansoftware.boomega.db.data.Record;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.objects.ObjectFilter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A Database object can communicate with a particular data source.
 *
 * <p>
 * It supports the CRUD operations with {@link Record}s.
 *
 * @author Daniel Gyorffy
 */
public interface Database {

    //CREATE, UPDATE, DELETE

    void insertRecord(@NotNull Record record);

    void updateRecord(@NotNull Record record);

    void removeRecord(@NotNull Record record);

    //READ

    int getTotalRecordCount();

    List<Record> getRecords();

    List<Record> getRecords(@NotNull FindOptions findOptions);

    List<Record> getRecords(@NotNull ObjectFilter objectFilter, @NotNull FindOptions findOptions);

    default List<Record> getRecords(int offSet, int size) {
        return getRecords(FindOptions.limit(offSet, size));
    }

    //OTHER

    /**
     * Checks whether the db is closed.
     *
     * @return `true` if closed; otherwise `false`.
     */
    boolean isClosed();

    /**
     * Closes the database
     */
    void close();

    /**
     * Returns a {@link DatabaseMeta} object that holds some meta-information
     * of the database.
     *
     * @return the DatabaseMeta object
     */
    DatabaseMeta getMeta();
}
