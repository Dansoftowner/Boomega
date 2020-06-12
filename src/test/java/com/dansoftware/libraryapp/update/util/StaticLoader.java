package com.dansoftware.libraryapp.update.util;

import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.libraryapp.update.loader.Loader;

public class StaticLoader implements Loader {

    private final String version;

    public StaticLoader(String version) {
        this.version = version;
    }

    @Override
    public UpdateInformation load() {
        return new UpdateInformation(version, null, null);
    }
}
