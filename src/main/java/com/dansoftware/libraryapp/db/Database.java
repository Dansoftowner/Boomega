package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.Magazine;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.objects.ObjectFilter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A Database object can communicate with a particular data source.
 *
 * <p>
 * It supports the CRUD operations with {@link Book} and {@link Magazine} objects.
 */
public interface Database {

    //CREATE, UPDATE, DELETE

    void insertMagazine(@NotNull Magazine magazine);

    void updateMagazine(@NotNull Magazine magazine);

    void removeMagazine(@NotNull Magazine magazine);

    void insertBook(@NotNull Book book);

    void updateBook(@NotNull Book book);

    void removeBook(@NotNull Book book);

    //READ

    int getTotalMagazineCount();

    int getTotalBookCount();

    List<Magazine> getMagazines();

    List<Magazine> getMagazines(FindOptions findOptions);

    List<Magazine> getMagazines(ObjectFilter objectFilter, FindOptions findOptions);

    List<Book> getBooks();

    List<Book> getBooks(FindOptions findOptions);

    List<Book> getBooks(ObjectFilter objectFilter, FindOptions findOptions);

    default List<Magazine> getMagazines(int offSet, int size) {
        return getMagazines(FindOptions.limit(offSet, size));
    }

    default List<Book> getBooks(int offSet, int size) {
        return getBooks(FindOptions.limit(offSet, size));
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
