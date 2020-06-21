package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.gui.util.node.UnEditableTextArea;
import com.dansoftware.libraryapp.util.adapter.ThrowableString;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

public class ExceptionHandler<T extends Event> implements EventHandler<T> {

    private final Throwable throwable;

    public ExceptionHandler(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void handle(T event) {
        Alert alert = new StyledAlert(Alert.AlertType.ERROR, throwable.getMessage());
        alert.setTitle("LibraryApp alert");
        alert.getDialogPane().setExpandableContent(
                new UnEditableTextArea(new ThrowableString(throwable).toString()));


        alert.show();
    }
}
