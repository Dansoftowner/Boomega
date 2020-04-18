package com.dansoftware.libraryapp.db.pojo;

import java.util.List;

public class Book {

    private int id;
    private int publishedYear;
    private int numberOfPages;
    private int numberOfCopies;

    private String title;
    private String language;
    private String notes;
    private String isbn;

    private Publisher publisher;
    private Subject subject;

    private List<Author> authors;

    public Book() {
    }

    private Book(Builder builder) {
        this.id = builder.id;
        this.publishedYear = builder.publishedYear;
        this.numberOfCopies = builder.numberOfCopies;
        this.numberOfPages = builder.numberOfPages;
        this.title = builder.title;
        this.language = builder.language;
        this.notes = builder.notes;
        this.isbn = builder.isbn;
        this.publisher = builder.publisher;
        this.subject = builder.subject;
        this.authors = builder.authors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public static class Builder {
        private int id;
        private int publishedYear;
        private int numberOfPages;
        private int numberOfCopies;

        private String title;
        private String language;
        private String notes;
        private String isbn;

        private Publisher publisher;
        private Subject subject;

        private List<Author> authors;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

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

        public Builder publisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public Builder authors(List<Author> authors) {
            this.authors = authors;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }


}
