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

package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.di.DIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalThemesConfigTest {

    @BeforeAll
    static void prepareDIService() {
        DIService.init();
    }

    @Test
    void testItStoresDefaultThemeClassName() {
        String expected = "com.x.x.Theme";

        var json = buildJson(expected);
        var config = new InternalThemesConfig(json);

        assertThat(config.getDefaultThemeClassName()).isEqualTo(expected);
        assertThat(config.getThemeClassNames()).isEqualTo(List.of(expected));
    }

    @Test
    void testItStoresAllPacks() {
        String defaultTheme = "x";
        List<String> themes = List.of("a", "b", "c");

        var json = buildJson(defaultTheme, themes);
        var config = new InternalThemesConfig(json);

        List<String> expected = Stream.of(List.of(defaultTheme), themes).flatMap(List::stream).toList();
        assertThat(config.getThemeClassNames()).isEqualTo(expected);
    }

    @Test
    void testDefaultThemeInstantiation() {
        Class<? extends Theme> clazz = LightTheme.class;
        String className = clazz.getName();

        var json = buildJson(className);
        var config = new InternalThemesConfig(json);

        Theme actual = config.getDefaultTheme();
        assertThat(actual).isExactlyInstanceOf(clazz);
    }

    private JsonObject buildJson(String defaultTheme) {
        return buildJson(defaultTheme, Collections.emptyList());
    }

    private JsonObject buildJson(String defaultTheme, List<String> themeClassNames) {
        var json = new JsonObject();
        json.addProperty(InternalThemesConfig.DEFAULT_THEME_CLASS_NAME, defaultTheme);
        json.add(InternalThemesConfig.THEME_CLASS_NAMES, toJsonArray(themeClassNames));
        return json;
    }

    private JsonArray toJsonArray(List<String> list) {
        var jsArray = new JsonArray();
        list.forEach(jsArray::add);
        return jsArray;
    }

}
