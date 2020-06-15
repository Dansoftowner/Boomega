package com.dansoftware.libraryapp.db;

import java.util.List;

/**
 * A Database object can communicate with a particular data source
 * and has the ability to load the data as {@link Book} objects.
 */
public interface Database {
    List<Book> getBooks(boolean fromCache);

}
