package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;

public interface WritingStrategy {
    void writeConfigurations(ConfigurationHolder holder) throws IOException;
}
