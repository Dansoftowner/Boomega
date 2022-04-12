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

package com.dansoftware.boomega.service.googlebooks;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Used for loading the data of multiple Google Books.
 *
 * @author Daniel Gyorffy
 */
@SuppressWarnings("ClassCanBeRecord")
public class GoogleBooksRequest {

    private final URL url;

    GoogleBooksRequest(URL url) {
        this.url = url;
    }

    public Volumes load() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return new Gson().fromJson(reader, Volumes.class);
        }
    }

    public boolean isEmpty() {
        return Pattern.compile(".*q=($|&.*)").matcher(this.url.toString()).matches();
    }

    @Override
    public String toString() {
        return this.url.toString();
    }
}
