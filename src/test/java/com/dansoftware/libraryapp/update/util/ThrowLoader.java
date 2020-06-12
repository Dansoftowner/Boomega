package com.dansoftware.libraryapp.update.util;

import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.libraryapp.update.loader.Loader;

public class ThrowLoader implements Loader {
    @Override
    public UpdateInformation load() throws Exception {
        throw new UnsupportedOperationException();
    }
}
