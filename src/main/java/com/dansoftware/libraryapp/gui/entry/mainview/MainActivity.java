package com.dansoftware.libraryapp.gui.entry.mainview;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.Context;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class MainActivity implements Context {

    private Database database;

    public MainActivity(@NotNull Database database) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
    }

    public void show() {

    }

    @Override
    public void showOverlay(Region region) {

    }

    @Override
    public void showOverlay(Region region, boolean blocking) {

    }

    @Override
    public void hideOverlay(Region region) {

    }

    @Override
    public void showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {

    }

    @Override
    public void showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {

    }

    @Override
    public void showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {

    }
}
