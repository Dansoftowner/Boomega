package com.dansoftware.libraryapp.appdata;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

public class ConfigurationKey<T> {

    public static final ConfigurationKey<String> DEFAULT_LOCALE =
            new ConfigurationKey<>("locale", "en", String::toString);

    public static final ConfigurationKey<Boolean> SEARCH_FOR_UPDATES_AT_START =
            new ConfigurationKey<>("searchupdates", true, Boolean::parseBoolean);

    public static final ConfigurationKey<File> CUSTOM_DB_FILE =
            new ConfigurationKey<>("custom_db", null, File::new);

    private String key;
    private T defaultValue;

    private Function<String, T> transformer;

    public ConfigurationKey(String key, T defaultValue, Function<String, T> transformer) {
        Objects.requireNonNull(key, "The 'key' parameter must not be null."::toString);

        this.key = key;
        this.defaultValue = defaultValue;
        this.transformer = transformer;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Function<String, T> getTransformer() {
        return transformer;
    }
}
