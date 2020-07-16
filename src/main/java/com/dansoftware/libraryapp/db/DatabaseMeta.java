package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.util.FileUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * A DatabaseMeta can hold all meta-information about a particular database.
 */
public class DatabaseMeta {
    private String name;
    private File file;

    public DatabaseMeta(@NotNull File file) {
        this(null, file);
    }

    public DatabaseMeta(@Nullable String name, @NotNull File file) {
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
        DatabaseMeta databaseMeta = (DatabaseMeta) o;
        return file.equals(databaseMeta.file);
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
