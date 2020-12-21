package com.dansoftware.libraryapp.db;

import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Id;

import java.util.List;
import java.util.Objects;

/**
 * A Book is a Plain Java Object that represents a <i>document</i> or a <i>record</i> in the database.
 *
 * <p>
 * Compatible with the <i>Nitrite</i> database api.
 */
public class Book {

    @Id
    private NitriteId id;

    private int publishedYear;
    private int numberOfPages;
    private int numberOfCopies;
    private String title;
    private String language;
    private String notes;
    private String isbn;
    private String publisher;
    private String subject;
    private List<String> authors;

    public Book() {
    }

    private Book(Builder builder) {
        this.title = Objects.requireNonNull(builder.title);
        this.authors = Objects.requireNonNull(builder.authors);
        this.publishedYear = builder.publishedYear;
        this.numberOfCopies = builder.numberOfCopies;
        this.numberOfPages = builder.numberOfPages;
        this.language = builder.language;
        this.notes = builder.notes;
        this.isbn = builder.isbn;
        this.publisher = builder.publisher;
        this.subject = builder.subject;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public NitriteId getId() {
        return id;
    }

    public void setId(NitriteId id) {
        this.id = id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() { }

        private int publishedYear;
        private int numberOfPages;
        private int numberOfCopies;

        private String title;
        private String language;
        private String notes;
        private String isbn;

        private String publisher;
        private String subject;

        private List<String> authors;

        public Builder publishedYear(int publishedYear) {
            this.publishedYear = publishedYear;
            return this;
        }

        public Builder numberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        public Builder numberOfCopies(int numberOfCopies) {
            this.numberOfCopies = numberOfCopies;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }
}
