package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;

public interface ReadingStrategy {
    void readConfigurationsTo(ConfigurationHolder holder) throws IOException;
}
