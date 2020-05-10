package com.dansoftware.libraryapp.db.util;

import java.io.File;
import java.util.Objects;

public class SqliteURLGenerator implements JDBCURLGenerator {

    private static final String PREFIX = "jdbc:sqlite:";

    private File dbFile;

    public SqliteURLGenerator(File dbFile) {
        this.dbFile = Objects.requireNonNull(dbFile, "The database file mustn't be null");
    }

    @Override
    public String getJDBCUrl() {
        return PREFIX + dbFile.getAbsolutePath();
    }
}
