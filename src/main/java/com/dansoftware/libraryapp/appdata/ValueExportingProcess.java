package com.dansoftware.libraryapp.appdata;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

/**
 * A ValueExportingProcess is used by {@link Preferences.Key} objects
 * to define how to export the particular value (java object) into a json-element.
 *
 * @param <T> defines what is the type of the value that we want to export to
 */
interface ValueExportingProcess<T> {

    /**
     * Creates a {@link ValueExportingProcess} that can export a value into
     * a {@link JsonElement} using {@link Gson}.
     *
     * @return the {@link ValueExportingProcess} object
     */
    static <T> ValueExportingProcess<T> defaultProcess() {
        return value -> new Gson().toJsonTree(value);
    }

    JsonElement export(@NotNull T value);
}
