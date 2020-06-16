package com.dansoftware.libraryapp.gui.entry.mainview;

import com.dansoftware.libraryapp.db.Database;

import java.util.Objects;

public class MainView {

    private Database database;

    public MainView(Database database) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
    }

    public void show() {

    }
}
