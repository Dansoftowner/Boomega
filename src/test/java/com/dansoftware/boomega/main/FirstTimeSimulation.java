package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.ConfigFile;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FirstTimeSimulation {

    private static final class NonExistingConfigFile extends ConfigFile {
        public NonExistingConfigFile() {
            super(StringUtils.EMPTY);
        }

        @Override
        public InputStream openStream() throws IOException {
            return InputStream.nullInputStream();
        }

        @Override
        public OutputStream openOutputStream() throws FileNotFoundException {
            return OutputStream.nullOutputStream();
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
