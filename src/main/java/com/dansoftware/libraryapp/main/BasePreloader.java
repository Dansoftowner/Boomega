package com.dansoftware.libraryapp.main;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public abstract class BasePreloader extends javafx.application.Preloader {
    //getParameters() hasParameters()

    protected List<String> getApplicationArgs() {
        return getParameters().getRaw();
    }

    protected void ifApplicationArgumentExist(@NotNull Consumer<String> action) {
        if (!getApplicationArgs().isEmpty()) {
            action.accept(getApplicationArgs().get(0));
        }
    }
}
