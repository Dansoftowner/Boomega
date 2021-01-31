package com.dansoftware.libraryapp.gui.record;

import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.googlebooks.Volume;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;

public class RecordValues {

    @NotNull
    private Record.Type recordType = Record.Type.BOOK;
    private String title = StringUtils.EMPTY;
    private String subtitle = StringUtils.EMPTY;
    private LocalDate publishedDate;
    private String publisher = StringUtils.EMPTY;
    private String magazineName = StringUtils.EMPTY;
    private String authors = StringUtils.EMPTY;
    private String language = StringUtils.EMPTY;
    private String isbn = StringUtils.EMPTY;
    private String subject = StringUtils.EMPTY;
    private String notes = StringUtils.EMPTY;
    private Integer numberOfCopies;
    private Integer numberOfPages;
    private Integer rating;

    private Volume volumeObject;

    public RecordValues recordType(@NotNull Record.Type recordType) {
        this.recordType = Objects.requireNonNull(recordType);
        return this;
    }

    public RecordValues title(String title) {
        this.title = StringUtils.getIfBlank(title, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues subtitle(String subtitle) {
        this.subtitle = StringUtils.getIfBlank(subtitle, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues date(LocalDate date) {
        this.publishedDate = date;
        return this;
    }

    public RecordValues publisher(String publisher) {
        this.publisher = StringUtils.getIfBlank(publisher, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues magazineName(String magazineName) {
        this.magazineName = StringUtils.getIfBlank(magazineName, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues authors(String authors) {
        this.authors = StringUtils.getIfBlank(authors, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues language(String language) {
        this.language = StringUtils.getIfBlank(language, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues isbn(String isbn) {
        this.isbn = StringUtils.getIfBlank(isbn, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues subject(String subject) {
        this.subject = StringUtils.getIfBlank(subject, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues notes(String notes) {
        this.notes = StringUtils.getIfBlank(notes, () -> StringUtils.EMPTY);
        return this;
    }

    public RecordValues numberOfCopies(Integer numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
        return this;
    }

    public RecordValues numberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
        return this;
    }

    public RecordValues rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public RecordValues googleVolumeObject(Volume volumeObject) {
        this.volumeObject = volumeObject;
        return this;
    }

    public Record.Type getRecordType() {
        return recordType;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getMagazineName() {
        return magazineName;
    }

    public String getAuthors() {
        return authors;
    }

    public String getLanguage() {
        return language;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSubject() {
        return subject;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public Integer getRating() {
        return rating;
    }

    public Volume getVolumeObject() {
        return volumeObject;
    }
}
