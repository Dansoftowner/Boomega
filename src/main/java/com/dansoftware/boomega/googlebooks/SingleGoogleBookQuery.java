package com.dansoftware.boomega.googlebooks;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Used for loading the data of one particular Google Book.
 *
 * @author Daniel Gyorffy
 */
public class SingleGoogleBookQuery {

    private final String url;

    public SingleGoogleBookQuery(@NotNull String url) {
        this.url = url;
    }

    public Volume load() throws IOException {
        try (var input = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            return new Gson().fromJson(input, Volume.class);
        }
    }

    @Override
    public String toString() {
        return url;
    }
}
