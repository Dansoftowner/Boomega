package com.dansoftware.libraryapp.gui.entry;

import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public interface Context {
    void showOverlay(Region region);

    void showOverlay(Region region, boolean blocking);

    void hideOverlay(Region region);

    void showErrorDialog(String title,
                         String message,
                         Consumer<ButtonType> onResult);

    void showErrorDialog(String title,
                         String message,
                         Exception exception,
                         Consumer<ButtonType> onResult);

    void showInformationDialog(String title,
                               String message,
                               Consumer<ButtonType> onResult);

    void requestFocus();

    default void showErrorDialog(String title, String message) {
        this.showErrorDialog(title, message, buttonType -> {});
    }

    default void showErrorDialog(String title, String message, Exception e) {
        this.showErrorDialog(title, message, e, buttonType -> {});
    }
}
