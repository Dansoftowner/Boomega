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

package com.dansoftware.boomega.gui.font;

import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

/**
 * Utility for loading the custom javaFX fonts.
 *
 * It's used because loading fonts in JavaFX CSS is not working
 * if the paths have spaces.
 * See:
 * <a href="https://stackoverflow.com/questions/33973921/javafx-font-face-css-error-loadstylesheetunprivileged/41753098">javafx @font-face css error “loadStyleSheetUnprivileged”</a>
 *
 * @author Daniel Gyorffy
 */
public class CustomFontsLoader {

    private static final List<String> fontPaths = List.of(
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Bold.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-BoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-ExtraBold.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-ExtraBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-ExtraLight.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-ExtraLightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Italic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Light.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-LightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Medium.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-MediumItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Regular.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-Thin.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMono-ThinItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Bold.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-BoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-ExtraBold.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-ExtraBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-ExtraLight.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-ExtraLightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Italic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Light.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-LightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Medium.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-MediumItalic.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Regular.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-Thin.ttf",
            "/com/dansoftware/boomega/gui/font/JetBrainsMonoNL-ThinItalic.ttf"
    );

    private CustomFontsLoader() {
    }

    @SuppressWarnings("ConstantConditions")
    public static void loadFonts() {
        fontPaths.stream().map(CustomFontsLoader.class::getResourceAsStream)
                .forEach(CustomFontsLoader::loadFont);
    }

    private static void loadFont(@NotNull InputStream source) {
        Font.loadFont(source, 12);
    }
}
