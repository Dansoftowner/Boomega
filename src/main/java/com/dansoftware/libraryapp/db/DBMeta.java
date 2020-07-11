package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.util.FileUtils;

import java.io.File;
import java.util.Objects;

public class DBMeta {
    private String name;
    private File file;

    public DBMeta(File file) {
        this(null, file);
    }

    public DBMeta(String name, File file) {
        this.file = Objects.requireNonNull(file, "file mustn't be null");
        this.name = Objects.isNull(name) ? file.getName() : name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBMeta dbMeta = (DBMeta) o;
        return file.equals(dbMeta.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file.toString());
    }

    @Override
    public String toString() {
        return this.name + " (" + FileUtils.shortenedFilePath(file, 1) + ")";
    }
}
