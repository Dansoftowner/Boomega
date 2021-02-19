package com.dansoftware.boomega.update;

import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.util.os.PlatformName;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Defines an {@link UpdateInformation} json-deserialization process for {@link Gson}.
 *
 * <p>
 * A json sample:
 * <pre>{@code
 * {
 *     "binaries" : {
 *       "Linux" : {
 *         "bundles" : {
 *           "deb" : {
 *             "downloadUrl" : "about:blank",
 *             "localeKey" : "-",
 *             "simpleName" : "Deb archieve"
 *           },
 *           "targz" : {
 *             "downloadUrl" : "about:blank",
 *             "localeKey" : "-",
 *             "simpleName" : "Tar.gz archieve"
 *           }
 *         }
 *       },
 *       "Windows" : {
 *         "bundles" : {
 *           "exe" : {
 *             "downloadUrl" : "about:blank",
 *             "localeKey" : "-",
 *             "simpleName" : "Exe installer"
 *           },
 *           "zip" : {
 *             "downloadUrl" : "about:blank",
 *             "localeKey" : "-",
 *             "simpleName" : "Zip archieve"
 *           }
 *         }
 *       }
 *     },
 *     "review" : {
 *       "defaultLang" : "en",
 *       "en" : "about:blank",
 *       "hu" : "about:blank"
 *     },
 *     "version" : "0.0.0"
 * }
 * } </pre>
 *
 * @author Daniel Gyorffy
 */
class UpdateInformationDeserializer implements JsonDeserializer<UpdateInformation> {

    //JSon element IDENTIFIERS

    private static final String BINARIES = "binaries";
    private static final String VERSION = "version";
    private static final String REVIEW = "review";

    private static final String BUNDLES = "bundles";
    private static final String SIMPLE_NAME = "simpleName";
    private static final String LOCALE_KEY = "localeKey";
    private static final String DOWNLOAD_URL = "downloadUrl";
    private static final String FILE_EXTENSION = "fileExtension";

    private static final String DEFAULT_LANG = "defaultLang";


    @NotNull
    @Override
    public UpdateInformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        //handling the Json as a JsonObject
        JsonObject jsonObject = json.getAsJsonObject();
        return new UpdateInformation(getVersion(jsonObject), getReviewUrl(jsonObject), getBinaries(jsonObject));
    }

    /**
     * Gets the version-string of the JsonObject.
     *
     * @param jsonObject the json-object
     * @return the version-string
     */
    private String getVersion(@NotNull JsonObject jsonObject) {
        return jsonObject.get(VERSION).getAsString();
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
        JsonElement reviewUrl = reviewBundle.get(Locale.getDefault().getLanguage());

        if (reviewUrl == null) {
            String defaultLang = reviewBundle.get(DEFAULT_LANG).getAsString();
            reviewUrl = reviewBundle.get(defaultLang);
        }

        return reviewUrl.getAsString();
    }

    /**
     * Retrieves the available downloadable binary-types from the JsonObject
     * into a {@link List}.
     */
    @NotNull
    private List<DownloadableBinary> getBinaries(@NotNull JsonObject jsonObject) {
        //the json object that contains the binaries for each platform
        JsonObject binaries = jsonObject.getAsJsonObject(BINARIES);

        //the json object that contains the binaries for the particular platform
        JsonObject osSpecificBinaries = binaries.getAsJsonObject(new PlatformName().toString());

        if (osSpecificBinaries == null)
            return Collections.emptyList();

        //the json object that contains the bundles for the particular platform
        JsonObject bundles = osSpecificBinaries.getAsJsonObject(BUNDLES);

        List<DownloadableBinary> result = new ArrayList<>(3);
        Set<Map.Entry<String, JsonElement>> entries = bundles.entrySet();
        entries.forEach(entry -> {
            //the json object that contains the information of the particular bundle
            JsonObject value = entry.getValue().getAsJsonObject();

            //simple name for example: "Exe installer", "Zip Archieve"
            String simpleName = value.get(SIMPLE_NAME).getAsString();
            //the localeKey for internationalization
            String localeKey = value.get(LOCALE_KEY).getAsString();
            //the url where we can find the actual resource
            String downloadUrl = value.get(DOWNLOAD_URL).getAsString();
            //it's file extension
            String fileExtension = value.get(FILE_EXTENSION).getAsString();

            //trying to localize the name...
            String localizedName;
            try {
                localizedName = I18N.getValue(localeKey);
            } catch (MissingResourceException e) {
                localizedName = simpleName;
            }

            result.add(new DownloadableBinary(localizedName, fileExtension, downloadUrl));
        });

        return result;
    }
}
