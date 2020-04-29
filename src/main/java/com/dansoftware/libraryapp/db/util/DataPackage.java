package com.dansoftware.libraryapp.db.util;

import com.dansoftware.libraryapp.db.pojo.Author;
import com.dansoftware.libraryapp.db.pojo.Book;
import com.dansoftware.libraryapp.db.pojo.Publisher;
import com.dansoftware.libraryapp.db.pojo.Subject;

import java.util.Collection;

public final class DataPackage {
    private Collection<Author> authors;
    private Collection<Publisher> publishers;
    private Collection<Subject> subjects;
    private Collection<Book> books;

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