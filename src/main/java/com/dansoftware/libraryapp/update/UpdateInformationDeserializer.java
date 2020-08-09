package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.locale.I18N;
import com.google.gson.*;
import com.sun.javafx.PlatformUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Defines an {@link UpdateInformation} json-deserialization process for {@link Gson}.
 *
 * @author Daniel Gyorffy
 */
public class UpdateInformationDeserializer implements JsonDeserializer<UpdateInformation> {

    //defining some JSON-key constants

    private static final String BINARIES = "binaries";
    private static final String VERSION = "version";
    private static final String REVIEW = "review";

    private static final String WINDOWS = "Windows";
    private static final String LINUX = "Linux";
    private static final String MAC = "Mac";
    private static final String BUNDLES = "bundles";
    private static final String SIMPLE_NAME = "simpleName";
    private static final String LOCALE_KEY = "localeKey";
    private static final String DOWNLOAD_URL = "downloadUrl";
    private static final String FILE_EXTENSION = "fileExtension";

    private static final String DEFAULT_LANG = "defaultLang";


    @NotNull
    @Override
    public UpdateInformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();

        return new UpdateInformation(
                getVersion(jsonObject),
                getReviewUrl(jsonObject),
                getBinaries(jsonObject)
        );
    }

    private String getVersion(@NotNull JsonObject jsonObject) {
        return jsonObject.get(VERSION).getAsString();
    }

    /**
     * Retrieves the available downloadable binary-types from the JsonObject
     * into a {@link Map}
     */
    @NotNull
    private List<DownloadableBinary> getBinaries(@NotNull JsonObject jsonObject) {
        JsonObject binaries = jsonObject.getAsJsonObject(BINARIES);

        JsonObject osSpecificBinaries;
        //
        if (PlatformUtil.isWindows()) {
            osSpecificBinaries = binaries.getAsJsonObject(WINDOWS);
        } else if (PlatformUtil.isLinux()) {
            osSpecificBinaries = binaries.getAsJsonObject(LINUX);
        } else if (PlatformUtil.isMac()) {
            osSpecificBinaries = binaries.getAsJsonObject(MAC);
        } else {
            return Collections.emptyList();
        }

        //
        JsonObject bundles = osSpecificBinaries.getAsJsonObject(BUNDLES);

        List<DownloadableBinary> result = new ArrayList<>(3);
        Set<Map.Entry<String, JsonElement>> entries = bundles.entrySet();
        entries.forEach(entry -> {
            JsonObject value = entry.getValue().getAsJsonObject();

            String simpleName = value.get(SIMPLE_NAME).getAsString();
            String localeKey = value.get(LOCALE_KEY).getAsString();
            String downloadUrl = value.get(DOWNLOAD_URL).getAsString();
            String fileExtension = value.get(FILE_EXTENSION).getAsString();

            String localizedName;
            try {
                localizedName = I18N.getGeneralWord(localeKey);
            } catch (MissingResourceException e) {
                localizedName = simpleName;
            }

            result.add(new DownloadableBinary(localizedName, fileExtension, downloadUrl));
        });

        return result;
    }

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


}
