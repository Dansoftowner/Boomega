/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.recordview;

import com.dansoftware.boomega.database.api.data.Record;
import com.dansoftware.boomega.rest.google.books.Volume;
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

    public Integer getRating() {
        return rating;
    }

    public Volume getVolumeObject() {
        return volumeObject;
    }
}
