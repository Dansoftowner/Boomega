package com.dansoftware.libraryapp.gui.entry.mainview;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.Context;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.Objects;
import java.util.function.Consumer;

public class MainActivity implements Context {

    private Database database;

    public MainActivity(Database database) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
    }

    public void show() {

    }

    @Override
    public void showOverlay(Region region) {

    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {

    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {

    }
}
