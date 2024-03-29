/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.rest.google.books;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Used for loading the data of one particular Google Book.
 *
 * @author Daniel Gyorffy
 */
@SuppressWarnings("ALL")
public class SingleGoogleBookQuery {

    private final URL url;

    public SingleGoogleBookQuery(@NotNull URL url) {
        this.url = url;
    }

    public Volume load() throws IOException {
        try (var input = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return new Gson().fromJson(input, Volume.class);
        }
    }

    @Override
    public String toString() {
        return url.toString();
    }
}
