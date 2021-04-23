/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.config.source;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * A {@link JsonFileSource} is a {@link ConfigSource} that allows to read/write configurations
 * from/into a particular JSON file.
 */
public class JsonFileSource extends JsonSource {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileSource.class);

    private final File file;
    private final JsonObject jsonBase;
    private final Gson gson;

    private final boolean created;

    public JsonFileSource(@NotNull File file) {
        this.file = file;
        this.gson = new Gson();
        this.created = createIfNotExists(file);
        this.jsonBase = readJsonBase(file);
    }

    private JsonObject readJsonBase(File file) {
        try (JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(openStream(file), StandardCharsets.UTF_8)))) {
            jsonReader.setLenient(true);
            JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            return jsonObject == null ? new JsonObject() : jsonObject;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            logger.error("Couldn't read file", e);
            return new JsonObject();
        }
    }

    private boolean createIfNotExists(File file) {
        try {
            file.getParentFile().mkdirs();
            return file.createNewFile();
        } catch (IOException e) {
            logger.error("Couldn't create file", e);
            return false;
        }
    }

    protected InputStream openStream(File file) throws IOException {
        return new FileInputStream(file);
    }

    protected OutputStream openOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public boolean isOpened() {
        return !created;
    }

    @Override
    public void commit() throws IOException {
        try (var writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(openOutputStream(file), StandardCharsets.UTF_8)))) {
            gson.toJson(this.jsonBase, writer);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected JsonObject getJsonBase() {
        return jsonBase;
    }
}
