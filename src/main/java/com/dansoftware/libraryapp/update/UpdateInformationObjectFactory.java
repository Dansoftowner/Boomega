package com.dansoftware.libraryapp.update;

import com.sun.javafx.PlatformUtil;
import org.apache.commons.io.IOUtils;
import org.json.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is responsible for creating object of {@link UpdateInformationObject} with
 * real data.
 *
 * @author Daniel Gyorffy
 */
public class UpdateInformationObjectFactory {

    /**
     * Don't let anyone to create an instance of this class
     */
    private UpdateInformationObjectFactory() {
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

        URL url = new URL(getUpdateInformationJSONPath());
        URLConnection connection = url.openConnection();

        JSONObject parsedJSON;
        try (InputStream inputStream = connection.getInputStream()) {
            String readJSON = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            parsedJSON = new JSONObject(readJSON);
        }

        String version = getVersion(parsedJSON);
        String reviewUrl = getReviewUrl(parsedJSON);
        Map<String, String> downloadableBinaries = getBinaries(parsedJSON);

        return new UpdateInformationObject(version, reviewUrl, downloadableBinaries);
    }

    /**
     * This method reads the version field of the JSONObject.
     *
     * @param json the JSONObject that should contain the
     *             necessary information about the update
     * @return the version of the new update
     */
    private String getVersion(JSONObject json) {
        return json.getString(UpdateJSONKey.VERSION);
    }

    /**
     * Reads the review webpage's url from the JSONObject.
     *
     * @param json the JSONObject that should contain the
     *             necessary information about the update
     * @return the url of review wepage that contains the features of the new update (for example: http://example.com/review.html)
     */
    private String getReviewUrl(JSONObject json) {
        return json.getString(UpdateJSONKey.REVIEW_PAGE);
    }

    /**
     * This method reads the location of the downloadable binaries of the new update
     * that are compatible with the operating system that the application runs on
     *
     * @param json the JSONObject that should contain the
     *             necessary information about the update
     * @return the Map that contains the binary file locations with their type (for example: {EXE: http://example/download.exe}
     */
    private Map<String, String> getBinaries(JSONObject json) {
        JSONObject binariesObject = json.getJSONObject(UpdateJSONKey.DOWNLOADABLE_BINARIES);

        Map<String, String> result = new HashMap<>();

        String platformName = null;
        if (PlatformUtil.isWindows()) platformName = "Windows";
        else if (PlatformUtil.isLinux()) platformName = "Linux";
        else if (PlatformUtil.isMac()) platformName = "Mac";

        Optional.ofNullable(platformName).ifPresent((var platform) -> {
            JSONObject platformBinaries = binariesObject.getJSONObject(platform);
            platformBinaries.keySet().forEach((var key) -> result.put(key, platformBinaries.getString(key)));
        });

        return result;
    }

    /**
     * Creates an {@link UpdateInformationObject} that contains all necessary information of the new
     * update.
     *
     * @return the object that contains the information
     * @throws IOException if some I/O exception occurs during reading the information from the server
     */
    public static UpdateInformationObject getInformation() throws IOException {
        return new UpdateInformationObjectFactory().parseJSON();
    }


}
