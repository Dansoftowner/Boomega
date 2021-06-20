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

package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.util.ReflectionUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JMetroTheme extends Theme {

    private static final Logger logger = LoggerFactory.getLogger(JMetroTheme.class);

    private static final List<String> JMETRO_STYLE_SHEETS;

    static {
        JMETRO_STYLE_SHEETS = getJMetroStyleSheets();
    }

    /**
     * Returns the jmetro stylesheet-paths through reflection.
     *
     * @return the {@link List} of stylesheets
     */
    private static List<String> getJMetroStyleSheets() {
        try {
            return getJMetroStyleSheetFields()
                    .peek(field -> field.setAccessible(true))
                    .map(ReflectionUtils::getDeclaredStaticValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Field> getJMetroStyleSheetFields() throws ReflectiveOperationException {
        return Stream.of(
                JMetro.class.getDeclaredField("BASE_STYLESHEET_URL"),
                JMetro.class.getDeclaredField("BASE_EXTRAS_STYLESHEET_URL"),
                JMetro.class.getDeclaredField("BASE_OTHER_LIBRARIES_STYLESHEET_URL"),
                Style.class.getDeclaredField("DARK_STYLE_SHEET_URL"),
                Style.class.getDeclaredField("LIGHT_STYLE_SHEET_URL")
        );
    }

    private final Style style;
    private final List<String> additionalStyleSheets;

    protected JMetroTheme(@NotNull Style style) {
        this.style = style;
        this.additionalStyleSheets = additionalStyleSheets();
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        scene.getStylesheets().removeAll(JMETRO_STYLE_SHEETS);
        scene.getStylesheets().removeAll(additionalStyleSheets);
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        parent.getStylesheets().removeAll(JMETRO_STYLE_SHEETS);
        parent.getStylesheets().removeAll(additionalStyleSheets);
    }

    @Override
    public void apply(@NotNull Scene scene) {
        new JMetro(style).setScene(scene);
        scene.getStylesheets().addAll(additionalStyleSheets);
    }

    @Override
    public void apply(@NotNull Parent parent) {
        new JMetro(style).setParent(parent);
        parent.getStylesheets().addAll(additionalStyleSheets);
    }

    @NotNull
    protected List<String> additionalStyleSheets() {
        return Collections.emptyList();
    }
}
