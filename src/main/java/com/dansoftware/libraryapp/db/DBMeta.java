package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.util.FileUtils;

import java.io.File;

public class DBMeta {
    private String name;
    private File file;

    public DBMeta() {
    }

    public DBMeta(File file) {
        this(file.getName(), file);
    }

    public DBMeta(String name, File file) {
        this.name = name;
        this.file = file;
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
    public String toString() {
        return this.name + " (" + FileUtils.shortenedFilePath(file, 1) + ")";
    }
}
