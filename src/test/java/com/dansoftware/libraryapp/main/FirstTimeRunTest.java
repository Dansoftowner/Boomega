package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ConfigFile;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

public class FirstTimeRunTest {

    private static final class NonExistingConfigFile extends ConfigFile {
        public NonExistingConfigFile() {
            super(StringUtils.EMPTY);
        }

        @Override
        public InputStream openStream() {
            return InputStream.nullInputStream();
        }

        @Override
        protected boolean determineExists() {
            return false;
        }
    }

    public static void main(String... args) {
        ConfigFile.setDefault(new NonExistingConfigFile());
        Main.main(args);
    }
}
