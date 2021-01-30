package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.data.Record;
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
