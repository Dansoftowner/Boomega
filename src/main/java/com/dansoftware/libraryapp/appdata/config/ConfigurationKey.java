package com.dansoftware.libraryapp.appdata.config;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * A ConfigurationKey is an accessor to a configuration-value.
 *
 * <p>
 *  Configuration keys used by {@link ConfigurationHolder} objects to access the concrete
 *  configuration values.
 *
 * <p>
 *  A ConfigurationKey object contains:
 *  <ul>
 *      <li>
 *          <b>A concrete String key</b><br>
 *          <i>
 *              because ConfigurationHolder objects
 *              are using String values to access the
 *              configurations in their private
 *              data structures
 *          </i>
 *          <br>
 *           <i>It can be accessed by the {@link ConfigurationKey#toString()} method</i>
 *          <br>
 *      </li>
 *      <li>
 *           <b>An object called 'transformer' with the type {@link Function}</b> <br>
 *           <i>
 *               because ConfigurationHolder objects hold the configurations as
 *               String literals so the transformer object can convert them to another object
 *               with an another type (the 'another type' is specified by the type parameter)
 *           </i>
 *      </li>
 *      <li>
 *          <b>A default value</b><br>
 *          <i>
 *              To specify a default value with the particular key.
 *              --->
 *              If there is no such configuration with the key in the
 *              particular ConfigurationHolder, the default value can
 *              be returned
 *          </i>
 *      </li>
 *  </ul>
 *
 * This class already contains some predefined ConfigurationKey constants.
 *
 * @param <T>
 * @author Daniel Gyorffy
 */
public class ConfigurationKey<T> {

    // -----> Predefined constants

    public static final ConfigurationKey<Locale> DEFAULT_LOCALE =
            new ConfigurationKey<>("locale", Locale::new, Locale.ENGLISH);

    public static final ConfigurationKey<Boolean> SEARCH_FOR_UPDATES_AT_START =
            new ConfigurationKey<>("searchupdates", Boolean::parseBoolean, true);

    public static final ConfigurationKey<File> CUSTOM_DB_FILE =
            new ConfigurationKey<>("custom_db", File::new, null);

    // <------

    private final String key;
    private final T defaultValue;

    private Function<String, T> transformer;

    public ConfigurationKey(String key, Function<String, T> transformer) {
        this(key, transformer, null);
    }

    public ConfigurationKey(String key, Function<String, T> transformer, T defaultValue) {
        Objects.requireNonNull(key, "The 'key' parameter must not be null."::toString);

        this.key = key;
        this.defaultValue = defaultValue;
        this.transformer = transformer;
    }

    /**
     * Acts as a getter for the {@link ConfigurationKey#key} field
     *
     * @return the concrete String-literal key
     */
    @Override
    public String toString() {
        return this.key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Function<String, T> getTransformer() {
        return transformer;
    }
}
