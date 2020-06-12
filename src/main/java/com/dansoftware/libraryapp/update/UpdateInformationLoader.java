package com.dansoftware.libraryapp.update;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is responsible for creating object of {@link UpdateInformationObject} with
 * real data.
 *
 * @author Daniel Gyorffy
 */
public class UpdateInformationLoader {

    /**
     * Don't let anyone to create an instance of this class
     */
    private UpdateInformationLoader() {
    }

    /**
     * This method returns the url of the JSON that contains
     * the information about the new update
     *
     * @return the url (for example: http://example.com/libapp-update.json)
     */
    private String getUpdateInformationJSONPath() {
        return null;
    }

    /**
     * This method reads the json from the internet then parses it and finally creates
     * a {@link UpdateInformationObject} with the data.
     *
     * @return the UpdateInformation object
     * @throws IOException if some I/O exception occurs during the reading
     */
    private UpdateInformationObject parseJSON() throws IOException {

        @SuppressWarnings("all")
        URL url = new URL(getUpdateInformationJSONPath());
        URLConnection connection = url.openConnection();

        Gson gson = new Gson();

        try (var reader = new InputStreamReader(connection.getInputStream())) {
            return gson.fromJson(reader, UpdateInformationObject.class);
        }
    }

    /**
     * Creates an {@link UpdateInformationObject} that contains all necessary information of the new
     * update.
     *
     * @return the object that contains the information
     * @throws IOException if some I/O exception occurs during reading the information from the server
     */
    public static UpdateInformationObject load() throws IOException {
        return new UpdateInformationLoader().parseJSON();
    }
}
