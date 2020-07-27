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

    private Map<String, String> getBinaries(@NotNull JsonObject jsonObject) {
        JsonObject binaries = jsonObject.getAsJsonObject(BINARIES);
        if (PlatformUtil.isWindows()) {
            binaries = jsonObject.getAsJsonObject(WINDOWS);
        } else if (PlatformUtil.isLinux()) {
            binaries = jsonObject.getAsJsonObject(LINUX);
        } else if (PlatformUtil.isMac()) {
            binaries = jsonObject.getAsJsonObject(MAC);
        }

        Map<String, String> result = new HashMap<>();

        Set<Map.Entry<String, JsonElement>> entries = binaries.entrySet();
        entries.forEach(entry -> {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            try {
                key = I18N.getGeneralWord(key);
            } catch (MissingResourceException ignored) {
                //
            }

            result.put(key, value.getAsString());
        });

        return result;
    }

    private String getReviewUrl(@NotNull JsonObject jsonObject) {
        JsonObject reviewBundle = jsonObject.getAsJsonObject(REVIEW);
        JsonElement review = reviewBundle.get(Locale.getDefault().getLanguage());

        if (review == null) {
            String defaultLang = reviewBundle.get(DEFAULT_LANG).getAsString();
            review = reviewBundle.get(defaultLang);
        }

        return review.getAsString();
    }


}
