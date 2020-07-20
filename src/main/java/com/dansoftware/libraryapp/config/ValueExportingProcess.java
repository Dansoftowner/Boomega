package com.dansoftware.libraryapp.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface ValueExportingProcess<T> {

    static <T> ValueExportingProcess<T> defaultProcess() {
        return value -> new Gson().toJsonTree(value);
    }

    JsonElement export(@NotNull T value);
}
