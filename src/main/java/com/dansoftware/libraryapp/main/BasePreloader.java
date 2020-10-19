package com.dansoftware.libraryapp.main;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public abstract class BasePreloader extends javafx.application.Preloader {

    protected List<String> getApplicationArgs() {
        return getParameters().getRaw();
    }

    protected boolean hasParameters() {
        return !getApplicationArgs().isEmpty();
    }

    protected void ifApplicationArgumentExist(@NotNull Consumer<String> action) {
        if (hasParameters())
            action.accept(getApplicationArgs().get(0));
    }
}
