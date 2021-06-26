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

package com.dansoftware.boomega.update;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.g00fy2.versioncompare.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * An {@link UpdateSearcher} that is used for actually searching for updates on the internet.
 *
 * @author Daniel Gyorffy
 */
class DefaultUpdateSearcher extends UpdateSearcher {

    /**
     * The location that points to the update-info resource
     */
    private static final String LOCATION = "https://raw.githubusercontent.com/Dansoftowner/Boomega/master/update.json";

    private final Version base;

    /**
     * Creates a basic update searcher object;
     *
     * @param base the base version that the object should compare to; mustn't be null
     * @throws NullPointerException if base is null.
     */
    public DefaultUpdateSearcher(@NotNull Version base) {
        this.base = Objects.requireNonNull(base, "The base mustn't be null");
    }

    @Override
    public @NotNull UpdateSearchResult search() {
        UpdateSearchResult result = new UpdateSearchResult();
        try {
            var information = loadInfo();
            if (information != null) {
                Version newVersion = new Version(information.getVersion());
                if (base.isLowerThan(newVersion)) {
                    result.setNewUpdate(true);
                    result.setInformation(information);
                }
            }
        } catch (IOException | RuntimeException e) {
            result.setFailed(true);
            result.setFailedCause(e);
        }

        return result;
    }

    @Override
    protected @Nullable UpdateInformation loadInfo() throws IOException {
        URL url = new URL(LOCATION);
        URLConnection connection = url.openConnection();
        connection.connect();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(UpdateInformation.class, new UpdateInformationDeserializer())
                .create();

        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return gson.fromJson(reader, UpdateInformation.class);
        }
    }
}
