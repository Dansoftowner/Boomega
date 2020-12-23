package com.dansoftware.libraryapp.googlebooks;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Used for loading the data of multiple Google Books.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBooksQuery {

    private final String url;

    GoogleBooksQuery(String url) {
        this.url = url;
    }

    public Volumes load() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            return new Gson().fromJson(reader, Volumes.class);
        }
    }
}
