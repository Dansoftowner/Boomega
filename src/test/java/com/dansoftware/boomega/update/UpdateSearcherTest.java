/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.update;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateSearcherTest {

    @ParameterizedTest
    @CsvSource({
            "0.0.0,0.0.1",
            "0.0.1,0.0.2",
            "0.1.0,0.2.0",
            "1.0.0,2.0.0",
            "2.1.6,2.1.7",
            "4.2.1,8.0.0"
    })
    void shouldHaveUpdate(String baseVersion, String updateVersion) {
        var searcher = buildUpdateSearcher(baseVersion, updateVersion);
        assertThat(searcher.search()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "0.0.1,0.0.0",
            "1.0.0,0.0.1",
            "2.0.1,2.0.0",
            "3.4.1,3.4.0",
            "8.1.22,8.1.0",
            "18.12.3,10.0.0"
    })
    void shouldHaveNoUpdate(String baseVersion, String updateVersion) {
        var searcher = buildUpdateSearcher(baseVersion, updateVersion);
        assertThat(searcher.search()).isNull();
    }

    private UpdateSearcher buildUpdateSearcher(String baseVersion, String updateVersion) {
        return new UpdateSearcher(new DummyReleasesFetcher(updateVersion), baseVersion);
    }

    private static class DummyReleasesFetcher implements ReleasesFetcher {

        private final String updateVersion;

        DummyReleasesFetcher(String updateVersion) {
            this.updateVersion = updateVersion;
        }

        @NotNull
        @Override
        public Releases fetchReleases() {
            Releases releases = new Releases();
            Release release = new Release();
            release.setVersion(updateVersion);
            releases.add(release);
            return releases;
        }
    }
}
