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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    @Test
    void testItStoresDefaultPackClassName() {
        String expected = "com.x.x.Pack";

        var json = buildJson(expected);
        var config = new InternalLanguagePacksConfig(json);

        assertThat(config.getFallbackLanguagePackClassName()).isEqualTo(expected);
        assertThat(config.getLanguagePackClassNames()).isEqualTo(List.of(expected));
    }

    @Test
    void testItStoresAllPacks() {
        String defaultPack = "x";
        List<String> packs = List.of("a", "b", "c");

        var json = buildJson(defaultPack, packs);
        var config = new InternalLanguagePacksConfig(json);

        List<String> expected = Stream.of(List.of(defaultPack), packs).flatMap(List::stream).toList();
        assertThat(config.getLanguagePackClassNames()).isEqualTo(expected);
    }

    @Test
    void testFallbackLanguagePackInstantiation() {
        String className = EnglishLanguagePack.class.getName();

        var json = buildJson(className);
        var config = new InternalLanguagePacksConfig(json);

        LanguagePack actual = config.getFallbackLanguagePack();
        assertThat(actual).isExactlyInstanceOf(EnglishLanguagePack.class);

        // test caching
        assertThat(config.getFallbackLanguagePack() == actual).isTrue();
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
