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

public class Preferences {

    private static Preferences DEFAULT;

    private final JsonObject jsonObject;
    private final File sourceFile;

    private Preferences(@NotNull File file) {
        this.sourceFile = file;

        try (var reader = new BufferedReader(new FileReader(file))) {
            JsonObject temp = new Gson().fromJson(reader, JsonObject.class);
            this.jsonObject = Objects.isNull(temp) ? new JsonObject() : temp;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Class for modifying/writing the data
     */
    public class Editor {

        private Editor() {
        }

        public <T> void set(@NotNull Key<T> key,
                            @Nullable T value) {
            put(key, value);
        }

        public <T> void put(@NotNull Key<T> key,
                            @Nullable T value) {
            JsonElement element = null;
            if (value != null)
                element = key.exportingProcess.export(value);

            Preferences.this.jsonObject.add(key.jsonKey, element);
        }

        public void putBoolean(String key, boolean value) {
            Preferences.this.jsonObject.addProperty(key, value);
        }

        public void putString(String key, String value) {
            Preferences.this.jsonObject.addProperty(key, value);
        }

        public void putInteger(String key, int value) {
            Preferences.this.jsonObject.addProperty(key, value);
        }

        public void putDouble(String key, double value) {
            Preferences.this.jsonObject.addProperty(key, value);
        }

        public void remove(@NotNull Key<?> key) {
            Preferences.this.jsonObject.remove(key.jsonKey);
        }

        public void remove(@NotNull String key) {
            Preferences.this.jsonObject.remove(key);
        }

        public void commit() throws IOException {
            try (var writer = new JsonWriter(new BufferedWriter(new FileWriter(sourceFile)))) {
                new Gson().toJson(Preferences.this.jsonObject, writer);
            } catch (JsonIOException e) {
                throw new IOException(e);
            }
        }
    }


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

    public static Preferences getPreferences() {
        if (Objects.isNull(DEFAULT))
            DEFAULT = getPreferences(new ConfigFile());
        return DEFAULT;
    }

    public static Preferences getPreferences(File configFile) {
        return new Preferences(FileUtils.createFile(configFile, false));
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
