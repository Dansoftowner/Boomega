package com.dansoftware.libraryapp.db;

import java.util.List;

/**
 * A Database object can communicate with a particular data source.
 *
 * <p>
 * It supports the CRUD operations with {@link Book} objects.
 */
public interface Database {

    /**
     * Inserts a book into the database.
     *
     * @param book the book to insert
     */
    void insertBook(Book book);

    /**
     * Updates a book in the database.
     *
     * @param book the book to update
     */
    void updateBook(Book book);

    /**
     * Removes a book from the database
     *
     * @param book the book to remove
     */
    void removeBook(Book book);

    /**
     * Loads all books into a {@link List}.
     *
     * @param fromCache if true the books will be loaded from a local cache.
     *                  Otherwise the books will be loaded directly from the database
     * @return the {@link Book} objects
     */
    List<Book> getBooks(boolean fromCache);

    /**
     * Removes all cache from memory.
     */
    void clearCache();

    /**
     * Checks whether the db is closed.
     * @return `true` if closed; otherwise `false`.
     */
    boolean isClosed();

    /**
     * Closes the database
     */
    void close();



}
