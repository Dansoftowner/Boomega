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

import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

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

    private static final String FONT_DIR = "/com/dansoftware/boomega/gui/font/poppins";

    private static final List<String> fontPaths = List.of(
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Bold.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-BoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-ExtraBold.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-ExtraBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-ExtraLight.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-ExtraLightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Italic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Light.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-LightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Medium.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-MediumItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Regular.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-Thin.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMono-ThinItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Bold.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-BoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-ExtraBold.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-ExtraBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-ExtraLight.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-ExtraLightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Italic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Light.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-LightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Medium.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-MediumItalic.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Regular.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-Thin.ttf",
            "/com/dansoftware/boomega/gui/font/jetbrains_mono/JetBrainsMonoNL-ThinItalic.ttf",

            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Black.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-BlackItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Bold.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-BoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-ExtraBold.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-ExtraBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-ExtraLight.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-ExtraLightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Italic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Light.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-LightItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Medium.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-MediumItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Regular.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-SemiBold.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-SemiBoldItalic.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-Thin.ttf",
            "/com/dansoftware/boomega/gui/font/poppins/Poppins-ThinItalic.ttf"

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
