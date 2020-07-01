package com.dansoftware.libraryapp.update.loader;

import com.dansoftware.libraryapp.update.UpdateInformation;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BaseLoader implements Loader {

    private static final String LOCATION = "https://update-server-ed6c3.firebaseio.com/libraryapp.json";

    @Override
    public UpdateInformation load() throws IOException {
        URL url = new URL(LOCATION);
        URLConnection connection = url.openConnection();

        Gson gson = new Gson();

        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return gson.fromJson(reader, UpdateInformation.class);
        }
    }
}
