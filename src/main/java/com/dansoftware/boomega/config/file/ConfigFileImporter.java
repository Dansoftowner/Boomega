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

package com.dansoftware.boomega.config.file;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConfigFileImporter {

    @SuppressWarnings("StatementWithEmptyBody")
    public void importFromZip(@NotNull File src, @NotNull ConfigFile configFile)
            throws IOException, InvalidZipContentException {

        Objects.requireNonNull(src, "src shouldn't be null");
        Objects.requireNonNull(configFile, "ConfigFile shouldn't be null");

        try (var input = new ZipInputStream(new BufferedInputStream(new FileInputStream(src)))) {

            ZipEntry zipEntry;
            while ((zipEntry = input.getNextEntry()) != null &&
                    !zipEntry.getName().equalsIgnoreCase(configFile.getName())) ;

            //at this point, we found the configuration-file zipEntry
            if (zipEntry != null)
                try (var reader = new BufferedInputStream(input);
                     var output = new BufferedOutputStream(new FileOutputStream(configFile))) {
                    byte[] buf = new byte[256];

                    int count;
                    while ((count = reader.read(buf)) >= 0) {
                        output.write(buf, 0, count);
                    }
                }
            else
                throw new InvalidZipContentException("The zip file does not contain a 'config.conf'");
        }
    }

    public static final class InvalidZipContentException extends Exception {
        InvalidZipContentException(String msg) {
            super(msg);
        }
    }
}
