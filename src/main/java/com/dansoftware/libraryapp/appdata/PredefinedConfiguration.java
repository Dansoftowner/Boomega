package com.dansoftware.libraryapp.appdata;

/**
 * This enum represents some basic configurations of the application.
 * These objects of the enum contains the keys and the default values
 * for the configurations/settings
 *
 * @see ConfigurationHandler
 */
public enum PredefinedConfiguration {
    DEFAULT_LOCALE("locale", "en"),
    SEARCH_FOR_UPDATES_AT_START("searchupdates", "true"),
    CUSTOM_DB_FILE("custom_db", null);

    private final String key;
    private final String defaultValue;

    PredefinedConfiguration(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
