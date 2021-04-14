package com.dansoftware.boomega.config;

import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.config.source.DefaultSource;
import com.dansoftware.boomega.config.source.InMemorySource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A Preferences object is a bridge between the application and the particular configuration-source (represented by {@link ConfigSource}).
 *
 * <p>
 * If we want to create a {@link Preferences} object that reads from the default config-file we can use the
 * static {@link #getPreferences()} method.
 *
 * <p>
 * For modifying the data we can use an {@link Editor} object that can be instantiated by the {@link #editor()} method.
 *
 * @author Daniel Gyorffy
 * @see ConfigFile
 */
public class Preferences {

    private static final Logger logger = LoggerFactory.getLogger(Preferences.class);

    private static Preferences defaultPrefs;

    private final ConfigSource source;

    Preferences(@NotNull ConfigSource source) {
        this.source = source;
    }

    /**
     * Creates an {@link Editor} object that can modify the data inside the {@link Preferences} object.
     *
     * @return the {@link Editor} object.
     */
    @NotNull
    public Editor editor() {
        return new Editor();
    }

    public <T> T get(@NotNull PreferenceKey<T> key) {
        return source.get(key);
    }

    @SuppressWarnings("unused")
    public String getString(@NotNull String key, String defValue) {
        return source.getString(key, defValue);
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(@NotNull String key, boolean defValue) {
        return source.getBoolean(key, defValue);
    }

    @SuppressWarnings("unused")
    public int getInteger(@NotNull String key, int defValue) {
        return source.getInteger(key, defValue);
    }

    @SuppressWarnings("unused")
    public double getDouble(@NotNull String key, double defValue) {
        return source.getDouble(key, defValue);
    }

    public ConfigSource getSource() {
        return source;
    }

    @NotNull
    public static synchronized Preferences getPreferences() {
        if (Objects.isNull(defaultPrefs)) {
            setDefault(buildDefaultPrefs());
        }
        return defaultPrefs;
    }

    public static synchronized void setDefault(@NotNull Preferences preferences) {
        Objects.requireNonNull(preferences, "The default preferences object shouldn't be null");
        defaultPrefs = preferences;
        logger.debug("Default preferences/config source set: '{}'", preferences.getSource().getClass().getName());
    }

    private static Preferences buildDefaultPrefs() {
        return new Preferences(new DefaultSource());
    }

    public static Preferences empty() {
        return new Preferences(new InMemorySource());
    }

    /**
     * An {@link Editor} used for modifying values in a {@link Preferences} object.
     *
     * <p>
     * If you make changes (put or delete values) it will immediately change the {@link Preferences} object
     * in the memory; but if you want to save those changes into the config-file you should use the {@link #commit()}
     * method.
     */
    public class Editor {

        private Editor() {
        }

        public <T> Editor modify(PreferenceKey<T> key, Consumer<T> modifier) {
            T value = get(key);
            modifier.accept(value);
            set(key, value);
            return this;
        }

        public <T> Editor set(@NotNull PreferenceKey<T> key,
                              @Nullable T value) {
            put(key, value);
            return this;
        }

        public <T> Editor put(@NotNull PreferenceKey<T> key,
                              @Nullable T value) {
            Preferences.this.source.put(key, value);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            Preferences.this.source.putBoolean(key, value);
            return this;
        }

        public Editor putString(String key, String value) {
            Preferences.this.source.putString(key, value);
            return this;
        }

        public Editor putInteger(String key, int value) {
            Preferences.this.source.putInteger(key, value);
            return this;
        }

        public Editor putDouble(String key, double value) {
            Preferences.this.source.putDouble(key, value);
            return this;
        }

        public Editor remove(@NotNull PreferenceKey<?> key) {
            Preferences.this.source.remove(key);
            return this;
        }

        public Editor remove(@NotNull String key) {
            Preferences.this.source.remove(key);
            return this;
        }

        /**
         * Writes all data into the config-file.
         *
         * @throws IOException if some I/o exception occurs
         */
        public void commit() throws IOException {
            Preferences.this.source.commit();
        }

        /**
         * Writes all data into the config-file.
         *
         * <p>
         * It is the same as {@link #commit()}, but it ignores exception.
         */
        public void tryCommit() {
            try {
                this.commit();
            } catch (IOException ignored) {
            }
        }
    }

}
