package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.util.CustomCloseable;

public interface AppConfigReader<R extends Throwable, C extends Exception> extends CustomCloseable<C> {
    AppConfig read() throws R;
}
