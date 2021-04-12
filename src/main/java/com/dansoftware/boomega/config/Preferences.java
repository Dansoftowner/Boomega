package com.dansoftware.boomega.config;

import com.dansoftware.boomega.gui.keybinding.KeyBindings;
import com.dansoftware.boomega.config.keybinding.KeyBindingsAdapter;
import com.dansoftware.boomega.config.logindata.LoginData;
import com.dansoftware.boomega.config.logindata.LoginDataAdapter;
import com.dansoftware.boomega.config.theme.ThemeAdapter;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.util.function.UncaughtSupplier;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An Preferences object is a bridge between the application and the configuration-source (config-file).
 *
 * <p>
 * If we want to create a {@link Preferences} object that reads from the default config-file we can use the
 * static {@link #getPreferences()} method, or if we want to read from a custom file we can use the {@link #getPreferences(File)}
 * method.
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

    /**
     * The backing JsonObject that actually holds the data
     */
    private final JsonObject jsonStorage;

    private final UncaughtSupplier<@NotNull InputStream, IOException> inputFactory;
    private final UncaughtSupplier<@NotNull OutputStream, IOException> outputFactory;
    private final Gson gson;

    private Preferences() throws IOException {
        this(InputStream::nullInputStream, OutputStream::nullOutputStream);
    }

    private Preferences(@NotNull UncaughtSupplier<@NotNull InputStream, IOException> inputFactory,
                        @NotNull UncaughtSupplier<@NotNull OutputStream, IOException> outputFactory) throws IOException {
        this.gson = createGson();
        this.inputFactory = Objects.requireNonNull(inputFactory);
        this.outputFactory = Objects.requireNonNull(outputFactory);
        this.jsonStorage = readIntoJsonObject(inputFactory.get());
    }

    private JsonObject readIntoJsonObject(@NotNull InputStream reader) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(reader, StandardCharsets.UTF_8)))) {
            jsonReader.setLenient(true);
            JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            return jsonObject == null ? new JsonObject() : jsonObject;
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Theme.class, new ThemeAdapter())
                .registerTypeAdapter(LoginData.class, new LoginDataAdapter())
                .registerTypeAdapter(KeyBindings.class, new KeyBindingsAdapter())
                .create();
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

    //

    // ------> Methods for reading data
    public <T> T get(@NotNull Key<T> key) {
        try {
            JsonElement jsonElement = this.jsonStorage.get(key.jsonKey);
            if (Objects.isNull(jsonElement)) {
                return key.defaultValue.get();
            }

            T value = gson.fromJson(jsonElement, key.type);
            return Objects.isNull(value) ? key.defaultValue.get() : value;
        } catch (RuntimeException e) {
            logger.error("Couldn't parse value for '{}'", key.jsonKey, e);
            return key.defaultValue.get();
        }
    }

    @SuppressWarnings("unused")
    public String getString(@NotNull String key, String defValue) {
        JsonElement jsonElement = this.jsonStorage.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsString();
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(@NotNull String key, boolean defValue) {
        JsonElement jsonElement = this.jsonStorage.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsBoolean();
    }

    @SuppressWarnings("unused")
    public int getInteger(@NotNull String key, int defValue) {
        JsonElement jsonElement = this.jsonStorage.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsInt();
    }

    @SuppressWarnings("unused")
    public double getDouble(@NotNull String key, double defValue) {
        JsonElement jsonElement = this.jsonStorage.get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsDouble();
    }

    public InputStream openInputStream() throws IOException {
        return inputFactory.get();
    }

    // <------
    @NotNull
    public static Preferences getPreferences() {
        if (Objects.isNull(defaultPrefs)) {
            try {
                ConfigFile configFile = ConfigFile.getConfigFile();
                defaultPrefs = new Preferences(configFile::openStream, configFile::openOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return defaultPrefs;
    }

    public static Preferences getOnlyOutputPreferences() {
        try {
            return new Preferences(InputStream::nullInputStream, () -> new FileOutputStream(ConfigFile.getConfigFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Preferences getPreferences(File configFile) throws IOException {
        return new Preferences(() -> new FileInputStream(configFile), () -> new FileOutputStream(configFile));
    }

    public static Preferences empty() throws IOException {
        return new Preferences();
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

        public <T> Editor modify(Key<T> key, Consumer<T> modifier) {
            T value = get(key);
            modifier.accept(value);
            set(key, value);
            return this;
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
                element = gson.toJsonTree(value, key.type);

            Preferences.this.jsonStorage.add(key.jsonKey, element);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            Preferences.this.jsonStorage.addProperty(key, value);
            return this;
        }

        public Editor putString(String key, String value) {
            Preferences.this.jsonStorage.addProperty(key, value);
            return this;
        }

        public Editor putInteger(String key, int value) {
            Preferences.this.jsonStorage.addProperty(key, value);
            return this;
        }

        public Editor putDouble(String key, double value) {
            Preferences.this.jsonStorage.addProperty(key, value);
            return this;
        }

        public Editor remove(@NotNull Key<?> key) {
            Preferences.this.jsonStorage.remove(key.jsonKey);
            return this;
        }

        public Editor remove(@NotNull String key) {
            Preferences.this.jsonStorage.remove(key);
            return this;
        }

        public Editor putFromInput(@NotNull Reader reader) {
            JsonObject readJson = new Gson().fromJson(reader, JsonObject.class);
            readJson.keySet().forEach(key -> Preferences.this.jsonStorage.add(key, readJson.get(key)));
            return this;
        }

        /**
         * Writes all data into the config-file.
         *
         * @throws IOException if some I/o exception occurs
         */
        public void commit() throws IOException {
            try (var writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(outputFactory.get(), StandardCharsets.UTF_8)))) {
                gson.toJson(Preferences.this.jsonStorage, writer);
            } catch (JsonIOException e) {
                throw new IOException(e);
            }
        }

        /**
         * Writes all data into the config-file.
         *
         * <p>
         * It is the same as {@link #commit()}, but it does not
         * throw any exception.
         */
        public void tryCommit() {
            try {
                this.commit();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * A {@link Key} is an accessor to a particular configuration.
     *
     * @param <T> the type of the object that can be accessed by the key
     */
    public static class Key<T> {


        private final String jsonKey;
        private final Class<T> type;
        private final Supplier<@NotNull T> defaultValue;

        public Key(@NotNull String jsonKey,
                   @NotNull Class<T> type,
                   @NotNull Supplier<@NotNull T> defaultValue) {
            this.jsonKey = jsonKey;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public static <T> Key<T> createKey(@NotNull Class<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
            return new Key<>(type.getName(), type, defaultValue);
        }

        /**
         * Key for accessing the default locale.
         */
        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class, Locale::getDefault);

        /**
         * Key for accessing the login data
         */
        public static final Key<LoginData> LOGIN_DATA = new Key<>("loginData", LoginData.class, LoginData::new);

        /**
         * Key for accessing that the automatic update-searching is turned on or off
         */
        public static final Key<Boolean> SEARCH_UPDATES = new Key<>("searchUpdates", Boolean.class, () -> Boolean.TRUE);

        /**
         * Key for accessing the configured theme
         */
        public static final Key<Theme> THEME = new Key<>("theme", Theme.class, Theme::getDefault);
    }
}
