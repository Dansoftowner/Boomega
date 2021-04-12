package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.ConfigFile;
import org.apache.commons.lang3.StringUtils;

public class FirstTimeRunTest {

    private static final class NonExistingConfigFile extends ConfigFile {
        public NonExistingConfigFile() {
            super(StringUtils.EMPTY);
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
