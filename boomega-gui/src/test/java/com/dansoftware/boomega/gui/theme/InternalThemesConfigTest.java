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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalThemesConfigTest {

    @BeforeAll
    static void prepareDIService() {
        DIService.init();
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForItStoresAllThemes")
    void testItStoresAllTheme(String defaultTheme, List<String> otherThemes, List<String> expected) {
        var json = buildJson(defaultTheme, otherThemes);
        var config = new InternalThemesConfig(json);

        assertThat(config.getThemeClassNames()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForThemeInstantiation")
    void testThemeInstantiation(
            Class<? extends Theme> defaultTheme,
            List<Class<? extends Theme>> otherThemes,
            List<Class<? extends Theme>> expected
    ) {
        String className = defaultTheme.getName();
        List<String> otherClassNames = otherThemes.stream().map(Class::getName).toList();

        var json = buildJson(className, otherClassNames);
        var config = new InternalThemesConfig(json);

        Theme actualDefault = config.getDefaultTheme();
        List<Theme> actualThemes = config.getThemes();

        assertThat(actualDefault).isExactlyInstanceOf(defaultTheme);
        assertThat(actualThemes.stream().map(Object::getClass).toList()).isEqualTo(expected);
    }

    private static Stream<Arguments> provideArgumentsForItStoresAllThemes() {
        return Stream.of(
                Arguments.of("x", Collections.emptyList(), List.of("x")),
                Arguments.of("x2", List.of("a"), List.of("x2", "a")),
                Arguments.of("x3", List.of("a", "b"), List.of("x3", "a", "b")),
                Arguments.of("x4", List.of("a", "b", "c"), List.of("x4", "a", "b", "c"))
        );
    }

    private static Stream<Arguments> provideArgumentsForThemeInstantiation() {
        // TODO: use dynamic class creation (e.g Byte Buddy)?
        return Stream.of(
                Arguments.of(
                        LightTheme.class,
                        Collections.emptyList(),
                        List.of(LightTheme.class)
                ),
                Arguments.of(
                        LightTheme.class,
                        List.of(DarkTheme.class),
                        List.of(LightTheme.class, DarkTheme.class)
                ),
                Arguments.of(
                        OsSynchronizedTheme.class,
                        List.of(LightTheme.class, DarkTheme.class),
                        List.of(OsSynchronizedTheme.class, LightTheme.class, DarkTheme.class)
                )
        );
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