package com.dansoftware.libraryapp.appdata;

import com.dansoftware.libraryapp.gui.entry.login.LoginData;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.FileUtils;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * An AppConfig object is a bridge between the application and the configuration-source (config-file).
 *
 * <p>
 * If we want to create a {@link Preferences} object that reads from the default config-file we can use the
 * static {@link #getPreferences()} method, or if we want to read from a custom file we can use the {@link #getPreferences(File)}
 * method.
 *
 * <p>
 * For modifying the data we can use an {@link Editor} object that can be instantiated by the {@link #editor()} method.
 *
 * @see ConfigFile#getFile()
 * @author Daniel Gyorffy
 */

public class Preferences {

    private static Preferences DEFAULT;

    /**
     * The backing JsonObject that actually holds the data
     */
    private final JsonObject jsonObject;

    /**
     * The file that holds the configurations (in JSON format)
     */
    private final File sourceFile;

    /**
     * Creates a normal preferences-object.
     *
     * <p>
     * Reads the configurations from the file into a backing {@link JsonObject}.
     *
     * @param file the file that holds the configurations
     * @throws IOException if some {@link IOException}, {@link JsonIOException} or {@link JsonSyntaxException} occurs.
     */
    private Preferences(@NotNull File file) throws IOException {
        this.sourceFile = file;

        try (var reader = new BufferedReader(new FileReader(file))) {
            JsonObject temp = new Gson().fromJson(reader, JsonObject.class);
            this.jsonObject = Objects.isNull(temp) ? new JsonObject() : temp;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * An {@link Editor} used for modifying values in a {@link Preferences} object.
     *
     * <p>
     * If you make changes (put or delete values) it will immediately change the {@link Preferences} object
     * in the memory; but if you want to save those changes into the config-file you should use the {@link #commit()}
     * method.
     *
     */
    public class Editor {

        private Editor() {
        }

        public <T> Editor set(@NotNull Key<T> key,
                            @Nullable T value) {
            put(key, value);
            return this;
        }

        public <T> Editor put(@NotNull Key<T> key,
                            @Nullable T value) {
            JsonElement element = null;
            if (value != null)
                element = key.exportingProcess.export(value);

            Preferences.this.jsonObject.add(key.jsonKey, element);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            Preferences.this.jsonObject.addProperty(key, value);
            return this;
        }

        public Editor putString(String key, String value) {
            Preferences.this.jsonObject.addProperty(key, value);
            return this;
        }

        public Editor putInteger(String key, int value) {
            Preferences.this.jsonObject.addProperty(key, value);
            return this;
        }

        public Editor putDouble(String key, double value) {
            Preferences.this.jsonObject.addProperty(key, value);
            return this;
        }

        public Editor remove(@NotNull Key<?> key) {
            Preferences.this.jsonObject.remove(key.jsonKey);
            return this;
        }

        public Editor remove(@NotNull String key) {
            Preferences.this.jsonObject.remove(key);
            return this;
        }

        /**
         * Writes all data into the config-file.
         *
         * @throws IOException if some I/o exception occurs
         */
        public void commit() throws IOException {
            try (var writer = new JsonWriter(new BufferedWriter(new FileWriter(sourceFile)))) {
                new Gson().toJson(Preferences.this.jsonObject, writer);
            } catch (JsonIOException e) {
                throw new IOException(e);
            }
        }

        /**
         * Writes all data into the config-file.
         *
         * <p>
         *  It is the same as {@link #commit()}, but it does not
         *  throw any exception.
         */
        public void tryCommit() {
            try {
                this.commit();
            } catch (IOException ignored) {
            }
        }
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

    // ------> Methods for reading data

    public <T> T get(@NotNull Key<T> key) {
        JsonElement jsonElement = this.jsonObject.get(key.jsonKey);
        if (Objects.isNull(jsonElement)) {
            return key.defaultValue.get();
        }

        T value = key.constructingProcess.construct(jsonElement);
        return Objects.isNull(value) ? key.defaultValue.get() : value;
    }

    @SuppressWarnings("unused")
    public String getString(@NotNull String key, String defValue) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsString();
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(@NotNull String key, boolean defValue) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsBoolean();
    }

    @SuppressWarnings("unused")
    public int getInteger(@NotNull String key, int defValue) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsInt();
    }

    @SuppressWarnings("unused")
    public double getDouble(@NotNull String key, double defValue) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsDouble();
    }

    // <------

    @NotNull
    public static Preferences getPreferences() {
        if (Objects.isNull(DEFAULT)) {
            try {
                DEFAULT = new Preferences(FileUtils.createFile(ConfigFile.getFile(), false));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DEFAULT;
    }

    @NotNull
    public static Preferences getPreferences(File configFile) throws IOException {
        return new Preferences(configFile);
    }

    /**
     * A {@link Key} is an accessor to a particular configuration.
     *
     * @param <T> the type of the object that can be accessed by the key
     */
    public static class Key<T> {

        private final String jsonKey;
        private final Supplier<@NotNull T> defaultValue;
        private final ValueConstructingProcess<T> constructingProcess;
        private final ValueExportingProcess<T> exportingProcess;

        public Key(@NotNull String jsonKey,
                   @NotNull Class<T> type,
                   @NotNull Supplier<@NotNull T> defaultValue) {
            this(jsonKey, defaultValue, ValueConstructingProcess.defaultProcess(type), ValueExportingProcess.defaultProcess());
        }

        public Key(@NotNull String jsonKey,
                   @NotNull Supplier<T> defaultValue,
                   @NotNull ValueConstructingProcess<T> constructingProcess,
                   @NotNull ValueExportingProcess<T> valueExportingProcess) {
            this.jsonKey = jsonKey;
            this.defaultValue = defaultValue;
            this.constructingProcess = constructingProcess;
            this.exportingProcess = valueExportingProcess;
        }

        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class, Locale::getDefault);
        public static final Key<LoginData> LOGIN_DATA = new Key<>("loginData", LoginData.class, LoginData::new);
        public static final Key<Boolean> SEARCH_UPDATES = new Key<>("searchUpdates", Boolean.class, () -> Boolean.TRUE);
        public static final Key<Theme> THEME = new Key<>("theme", Theme::getDefault, element -> {
            try {
                Class<?> themeClass = PluginClassLoader.getInstance().loadClass(element.getAsString());
                Constructor<?> constructor = themeClass.getConstructor();
                constructor.setAccessible(true);
                return (Theme) constructor.newInstance();
            } catch (ReflectiveOperationException | ClassCastException e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return null;
            }
        }, value -> new JsonPrimitive(value.getClass().getName()));

        public static <T> Key<T> getKey(@NotNull Class<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
            return new Key<>(type.getName(), type, defaultValue);
        }
    }
}
