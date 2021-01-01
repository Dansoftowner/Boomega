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

    void insertMagazine(@NotNull Magazine magazine);

    void updateMagazine(@NotNull Magazine magazine);

    void removeMagazine(@NotNull Magazine magazine);

    /**
     * Loads all magazines into a {@link List}.
     *
     * @param fromCache if true the books will be loaded from a local cache.
     * @return the {@link Magazine} objects
     */
    List<Magazine> getMagazines(boolean fromCache);

    List<Magazine> getMagazines(FindOptions findOptions);

    List<Magazine> getMagazines(ObjectFilter objectFilter, FindOptions findOptions);

    void insertBook(@NotNull Book book);

    void updateBook(@NotNull Book book);

    void removeBook(@NotNull Book book);

    /**
     * Loads all books into a {@link List}.
     *
     * @param fromCache if true the books will be loaded from a local cache.
     *                  Otherwise the books will be loaded directly from the database
     * @return the {@link Book} objects
     */
    List<Book> getBooks(boolean fromCache);

    List<Book> getBooks(FindOptions findOptions);

    List<Book> getBooks(ObjectFilter objectFilter, FindOptions findOptions);

    /**
     * Removes all cache from memory.
     */
    void clearCache();

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
