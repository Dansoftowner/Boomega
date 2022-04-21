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

package com.dansoftware.boomega.gui.font;

import com.dansoftware.boomega.util.Collections;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.stream.Stream;

import static com.dansoftware.boomega.util.Resources.resJson;

/**
 * Utility for loading the custom javaFX fonts.
 * <p>
 * It's used because loading fonts in JavaFX CSS is not working
 * if the paths have spaces.
 * See:
 * <a href="https://stackoverflow.com/questions/33973921/javafx-font-face-css-error-loadstylesheetunprivileged/41753098">javafx @font-face css error “loadStyleSheetUnprivileged”</a>
 *
 * @author Daniel Gyorffy
 */
public class CustomFontsLoader {

    private static final Logger logger = LoggerFactory.getLogger(CustomFontsLoader.class);
    private static final String FONT_PATHS_CONFIG = "fonts_to_load.json";

    private static Stream<String> getFontPaths() {
        JsonArray jsonArray = resJson(FONT_PATHS_CONFIG, CustomFontsLoader.class).getAsJsonArray();
        return Collections.toImmutableList(jsonArray).stream().map(JsonElement::getAsString);
    }

    @SuppressWarnings("ConstantConditions")
    public static void loadFonts() {
        try {
            getFontPaths()
                    .map(CustomFontsLoader.class::getResource)
                    .peek(it -> logger.debug("Loading font '{}'", it.toExternalForm()))
                    .map(CustomFontsLoader::openStream)
                    .forEach(CustomFontsLoader::loadFont);
        } catch (UncheckedIOException | NullPointerException e) {
            logger.error("Couldn't load internal fonts", e);
        }
    }

    private static InputStream openStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void loadFont(@NotNull InputStream source) {
        Font.loadFont(source, 12);
    }

    private CustomFontsLoader() {
        // Do not instantiate
    }
}
