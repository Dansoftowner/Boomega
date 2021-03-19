package com.dansoftware.boomega.db;

import com.jfilegoodies.FileGoodies;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * A DatabaseMeta can hold all meta-information about a particular database.
 */
public class DatabaseMeta {
    private String name;
    private File file;

    private String stringFormat;

    public DatabaseMeta(@NotNull File file) {
        this.file = Objects.requireNonNull(file, "file mustn't be null");
        this.name = FilenameUtils.getBaseName(file.getName());
    }

    public DatabaseMeta(@NotNull String name, @NotNull File file) {
        this.file = Objects.requireNonNull(file, "file mustn't be null");
        this.name = Objects.requireNonNull(name, "name mustn't be null");
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
        DatabaseMeta databaseMeta = (DatabaseMeta) o;
        return file.equals(databaseMeta.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file.toString());
    }

    @Override
    public String toString() {
        if (this.stringFormat == null)
            this.stringFormat = this.name + " (" + FileGoodies.shortenedFilePath(file, 1) + ")";
        return this.stringFormat;
    }

    public static DatabaseMeta parseFrom(String filePath) {
        if (filePath == null)
            return null;

        var file = new File(filePath);
        /*if (!file.exists()) {
            return null;
        }*/

        return new DatabaseMeta(file);
    }
}
