package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.log.GuiLog;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateInformationObjectFactory {

    private static final Logger LOGGER = Logger.getLogger(UpdateInformationObjectFactory.class.getName());

    private String getUpdateInformationJSONPath() {
        return null;
    }

    public UpdateInformationObject parseJSON() {

        try {
            URL url = new URL(getUpdateInformationJSONPath());
            URLConnection connection = url.openConnection();

            JSONObject parsedJSON;
            try (InputStream inputStream = connection.getInputStream()) {
                String readJSON = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                parsedJSON = new JSONObject(readJSON);
            }

            String version = getValueFromJSON(parsedJSON, UpdateInformationJSONKey.VERSION);
            String reviewUrl = getValueFromJSON(parsedJSON, UpdateInformationJSONKey.REVIEW_PAGE);
            String downloadableJarPath = getValueFromJSON(parsedJSON, UpdateInformationJSONKey.DOWNLOADABLE_INSTALLER);

            return new UpdateInformationObject(version, downloadableJarPath, reviewUrl);

        } catch (IOException e) {
            LOGGER.log(new GuiLog(Level.SEVERE, e, "Update checker", "update.cantsearch"));
        }

        return null;
    }


    private String getValueFromJSON(JSONObject jsonObject, UpdateInformationJSONKey key) {
        return jsonObject.getString(key.toString());
    }


    public static UpdateInformationObject getInformation() {
        return new UpdateInformationObjectFactory().parseJSON();
    }


}
