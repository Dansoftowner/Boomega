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

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigFileCompressor {

    public void writeToZip(ConfigFile src, File outputFile) throws IOException {
        try (var prefInput = new BufferedInputStream(src.openStream());
             var prefOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {

            ZipEntry zipEntry = new ZipEntry(src.getName());
            prefOutput.putNextEntry(zipEntry);

            byte[] buf = new byte[256];

            int byteCount;
            while ((byteCount = prefInput.read(buf)) >= 0) {
                prefOutput.write(buf, 0, byteCount);
            }
        }
    }

}
