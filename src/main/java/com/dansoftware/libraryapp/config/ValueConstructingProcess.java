package com.dansoftware.libraryapp.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

interface ValueConstructingProcess<T> {

    static <T> ValueConstructingProcess<T> defaultProcess(Class<T> type) {
        return element -> new Gson().fromJson(element.toString(), type);
    }

    T construct(@NotNull JsonElement element);
}
