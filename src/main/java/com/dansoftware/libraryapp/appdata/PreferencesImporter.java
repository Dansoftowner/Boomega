package com.dansoftware.libraryapp.appdata;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Daniel Gyorffy
 */
public class PreferencesImporter {

    private final Preferences target;

    public PreferencesImporter(@NotNull Preferences target) {
        this.target = target;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void importFromZip(@NotNull File src, @NotNull Preferences to)
            throws IOException, InvalidZipContentException {

        Objects.requireNonNull(src, "src shouldn't be null");
        Objects.requireNonNull(to, "Preferences shouldn't be null");

        try (var input = new ZipInputStream(new BufferedInputStream(new FileInputStream(src)))) {

            ZipEntry zipEntry;
            while ((zipEntry = input.getNextEntry()) != null &&
                    !zipEntry.getName().equalsIgnoreCase("config.conf")) ;

            //at this point, we found the 'config.conf' zipEntry
            if (zipEntry != null)
                try (var reader = new InputStreamReader(input)) {
                    to.editor().putFromInput(reader).commit();
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
