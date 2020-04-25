package com.dansoftware.libraryapp.db.util;

import java.io.File;

public class SqliteURLGenerator implements JDBCURLGenerator {

    private static final String PREFIX = "jdbc:sqlite";

    private File dbFile;

    public SqliteURLGenerator(File dbFile) {
        this.dbFile = dbFile;
    }

    @Override
    public String getJDBCUrl() {
        return PREFIX + dbFile.getAbsolutePath();
    }
}
