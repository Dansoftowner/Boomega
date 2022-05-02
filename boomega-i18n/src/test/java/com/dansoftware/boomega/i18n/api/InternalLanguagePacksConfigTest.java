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

package com.dansoftware.boomega.i18n.api;

import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.i18n.EnglishLanguagePack;
import com.dansoftware.boomega.i18n.HungarianLanguagePack;
import com.dansoftware.boomega.i18n.TurkishLanguagePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.dansoftware.boomega.i18n.api.InternalLanguagePacksConfig.ALL_PACKS_KEY;
import static com.dansoftware.boomega.i18n.api.InternalLanguagePacksConfig.FALLBACK_PACK_KEY;
import static org.assertj.core.api.Assertions.assertThat;

public class InternalLanguagePacksConfigTest {

    @BeforeAll
    static void prepareDIService() {
        DIService.init();
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForItStoresAllPacks")
    void testItStoresAllPacks(String defaultPack, List<String> otherPacks, List<String> expected) {
        var json = buildJson(defaultPack, otherPacks);
        var config = new InternalLanguagePacksConfig(json);

        assertThat(config.getLanguagePackClassNames()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForPackInstantiation")
    void testLanguagePackInstantiation(
            Class<? extends LanguagePack> defaultPack,
            List<Class<? extends LanguagePack>> otherPacks,
            List<Class<? extends LanguagePack>> expected
    ) {
        String className = defaultPack.getName();

        var json = buildJson(className, otherPacks.stream().map(Class::getName).toList());
        var config = new InternalLanguagePacksConfig(json);

        LanguagePack actualDefault = config.getFallbackLanguagePack();
        List<LanguagePack> actualPacks = config.getLanguagePacks();

        assertThat(actualDefault).isExactlyInstanceOf(defaultPack);
        assertThat(actualPacks.stream().map(Object::getClass).toList()).isEqualTo(expected);
    }

    private static Stream<Arguments> provideArgumentsForItStoresAllPacks() {
        return Stream.of(
                Arguments.of("x", Collections.emptyList(), List.of("x")),
                Arguments.of("x2", List.of("a"), List.of("x2", "a")),
                Arguments.of("x3", List.of("a", "b"), List.of("x3", "a", "b")),
                Arguments.of("x4", List.of("a", "b", "c"), List.of("x4", "a", "b", "c"))
        );
    }

    private static Stream<Arguments> provideArgumentsForPackInstantiation() {
        // TODO: use dynamic class creation (e.g Byte Buddy)?
        return Stream.of(
                Arguments.of(
                        EnglishLanguagePack.class,
                        Collections.emptyList(),
                        List.of(EnglishLanguagePack.class)
                ),
                Arguments.of(
                        HungarianLanguagePack.class,
                        List.of(EnglishLanguagePack.class),
                        List.of(HungarianLanguagePack.class, EnglishLanguagePack.class)
                ),
                Arguments.of(
                        TurkishLanguagePack.class,
                        List.of(EnglishLanguagePack.class, HungarianLanguagePack.class),
                        List.of(TurkishLanguagePack.class, EnglishLanguagePack.class, HungarianLanguagePack.class)
                )
        );
    }

    private JsonObject buildJson(String defaultPack) {
        return buildJson(defaultPack, Collections.emptyList());
    }

    private JsonObject buildJson(String defaultPack, List<String> allPacks) {
        var json = new JsonObject();
        json.addProperty(FALLBACK_PACK_KEY, defaultPack);
        json.add(ALL_PACKS_KEY, toJsonArray(allPacks));
        return json;
    }

    private JsonArray toJsonArray(List<String> list) {
        var jsArray = new JsonArray();
        list.forEach(jsArray::add);
        return jsArray;
    }
}