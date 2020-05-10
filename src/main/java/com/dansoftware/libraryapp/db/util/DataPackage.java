package com.dansoftware.libraryapp.db.util;

import com.dansoftware.libraryapp.db.pojo.*;

import java.util.Collection;

/**
 * A DataPackage can store database objects in the memory.
 *
 * <p>
 * A DataPackage can store all (4) type of database objects:
 * <ul>
 *     <li>{@link Author} objects</li>
 *     <li>{@link Publisher} objects</li>
 *     <li>{@link Subject} objects</li>
 *     <li>{@link Book} objects</li>
 * </ul>
 *
 * @see DBConnection#loadAllData() DBConnection objects uses DataPackgage
 * to store the read data from the database
 */
public final class DataPackage {

    private final Collection<Author> authors;
    private final Collection<Publisher> publishers;
    private final Collection<Subject> subjects;
    private final Collection<Book> books;

    /**
     * Calls the {@link DataPackage#DataPackage(Collection, Collection, Collection, Collection)}
     * constructor with {@link RecordCollection} objects.
     *
     * @see DataPackage#DataPackage(Collection, Collection, Collection, Collection)
     * @see RecordCollection
     */
    public DataPackage() {
        this(
                new RecordCollection<>(),
                new RecordCollection<>(),
                new RecordCollection<>(),
                new RecordCollection<>()
        );
    }

    /**
     * Creates a DataPackage with initial data.
     *
     * @param authors    the initial list of authors
     * @param publishers the initial list of publishers
     * @param subjects   the initial list of subjects
     * @param books      the initial list of books
     */
    public DataPackage(Collection<Author> authors, Collection<Publisher> publishers, Collection<Subject> subjects, Collection<Book> books) {
        this.authors = authors;
        this.publishers = publishers;
        this.subjects = subjects;
        this.books = books;
    }

    public Collection<Author> getAuthors() {
        return authors;
    }

    public Collection<Publisher> getPublishers() {
        return publishers;
    }

    public Collection<Subject> getSubjects() {
        return subjects;
    }

    public Collection<Book> getBooks() {
        return books;
    }
}