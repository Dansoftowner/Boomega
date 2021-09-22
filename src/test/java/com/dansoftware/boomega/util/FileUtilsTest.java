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

package com.dansoftware.boomega.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {

    @Test
    void testJoinToFilePath() {
        String[] items = new String[] { "A", "B", "C" };
        String expected = String.join(File.separator, items);
        String actual = FileUtils.joinToFilePath(items);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testShortenedPath() {
        File file = new File("/A/B/C/D/E");

        String excepted = ".../C/D/E";
        String actual = FileUtils.shortenedPath(file, 3, "...", "/");

        assertThat(actual).isEqualTo(excepted);
    }
}
