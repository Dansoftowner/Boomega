package com.dansoftware.boomega.config;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Daniel Gyorffy
 */
public class PreferencesExporter {

    private final Preferences src;

    public PreferencesExporter(@NotNull Preferences src) {
        this.src = src;
    }

    public void writeToZip(File outputFile) throws IOException {
        try (var prefInput = new BufferedInputStream(src.openInputStream());
             var prefOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {

            ZipEntry zipEntry = new ZipEntry("config.conf");
            prefOutput.putNextEntry(zipEntry);

            byte[] buf = new byte[256];

            int byteCount;
            while ((byteCount = prefInput.read(buf)) >= 0) {
                prefOutput.write(buf, 0, byteCount);
            }
        }
    }
}
