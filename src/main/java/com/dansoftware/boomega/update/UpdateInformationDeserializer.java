/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.update;

import com.dansoftware.boomega.util.os.OsInfo;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Defines an {@link UpdateInformation} json-deserialization process for {@link Gson}.
 *
 * @author Daniel Gyorffy
 */
class UpdateInformationDeserializer implements JsonDeserializer<UpdateInformation> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateInformationDeserializer.class);

    //JSon element IDENTIFIERS

    private static final String PACKS = "packs";
    private static final String VERSION = "version";
    private static final String WEBSITE = "website";
    private static final String REVIEW = "review";

    private static final String SIMPLE_NAME = "name";
    private static final String SCOPE = "scope";
    private static final String DOWNLOAD_URL = "url";
    private static final String SIZE = "size";
    private static final String FILE_EXTENSION = "fileExtension";

    private static final String DEFAULT_LANG = "defaultLang";


    @NotNull
    @Override
    public UpdateInformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        //handling the Json as a JsonObject
        JsonObject jsonObject = json.getAsJsonObject();
        return new UpdateInformation(getVersion(jsonObject), getReviewUrl(jsonObject), getWebsite(jsonObject), getPacks(jsonObject));
    }

    /**
     * Gets the version-string of the JsonObject.
     *
     * @param jsonObject the json-object
     * @return the version-string
     */
    private String getVersion(@NotNull JsonObject jsonObject) {
        String versionString = jsonObject.get(VERSION).getAsString();
        logger.debug("Version string read: '{}'", versionString);
        return versionString;
    }

    /**
     * Gets the right url of the markdown-text review resource.
     *
     * @param jsonObject the jsonObject
     * @return the url
     */
    @NotNull
    private String getReviewUrl(@NotNull JsonObject jsonObject) {
        JsonObject reviewBundle = jsonObject.getAsJsonObject(REVIEW);

        logger.debug("Finding review for the current locale...");
        JsonElement reviewUrl = reviewBundle.get(Locale.getDefault().getLanguage());

        if (reviewUrl == null) {
            logger.debug("Didn't find review for the current locale; falling back to the default one");
            String defaultLang = reviewBundle.get(DEFAULT_LANG).getAsString();
            reviewUrl = reviewBundle.get(defaultLang);
        }

        String asString = reviewUrl.getAsString();
        logger.debug("Review url read: '{}'", asString);
        return asString;
    }

    @NotNull
    private String getWebsite(@NotNull JsonObject jsonObject) {
        return jsonObject.get(WEBSITE).getAsString();
    }

    /**
     * Retrieves the available downloadable binary-types from the JsonObject
     * into a {@link List}.
     */
    @NotNull
    private List<DownloadableBinary> getPacks(@NotNull JsonObject jsonObject) {
        logger.debug("Reading packs...");

        //the json object that contains the binaries for each platform
        JsonArray jsonPacks = jsonObject.getAsJsonArray(PACKS);

        List<DownloadableBinary> packs = new LinkedList<>();
        jsonPacks.forEach(jsonElement -> {
            JsonObject asObject = jsonElement.getAsJsonObject();
            String name = asObject.get(SIMPLE_NAME).getAsString();
            logger.debug("Found pack '{}'", name);

            JsonElement downloadUrlJson = asObject.get(DOWNLOAD_URL);
            if (downloadUrlJson != null && !downloadUrlJson.isJsonNull()) {
                logger.debug("Pack has url");
                JsonArray scope = asObject.get(SCOPE).getAsJsonArray();
                if (scope.contains(new JsonPrimitive(getOsId())) || scope.contains(new JsonPrimitive("*"))) {
                    logger.debug("Pack is compatible with the current os");
                    String fileExtension = asObject.get(FILE_EXTENSION).getAsString();
                    String downloadUrl = downloadUrlJson.getAsString();
                    int size = asObject.get(SIZE).getAsInt();
                    logger.debug("File extension: '{}' , download url: '{}'", fileExtension, downloadUrl);
                    packs.add(new DownloadableBinary(name, fileExtension, downloadUrl, size));
                }
            }
        });
        return packs;
    }

    private String getOsId() {
        return OsInfo.isWindows() ? "win" : OsInfo.isLinux() ? "linux" : OsInfo.isMac() ? "mac" : "*";
    }
}
