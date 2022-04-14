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

package com.dansoftware.boomega.util;

import com.dansoftware.boomega.util.os.OsInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

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
        String actual = FileUtils.shortenedPath(file, 2, "...", "/");

        assertThat(actual).isEqualTo(excepted);
    }

    @Test
    void testIsExecutable() {
        Stream<String> executables =
                OsInfo.isWindows() ? Stream.of("exe", "msi") :
                OsInfo.isLinux() ? Stream.of("deb", "rpm") :
                        OsInfo.isMac() ? Stream.of("dmg", "app") :
                                Stream.empty();

        executables.map(ext -> new File("file." + ext))
                .forEach(it -> assertThat(FileUtils.isExecutable(it)).isTrue());
    }

    @Test
    void testIsNotExecutable() {
        Stream.of("txt", "docx", "xlsx")
                .map(ext -> new File("file." + ext))
                .forEach(it -> assertThat(FileUtils.isExecutable(it)).isFalse());
    }
}
