package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.util.CustomCloseable;

public interface AppConfigWriter<W extends Throwable, C extends Exception> extends CustomCloseable<C> {
    void write(AppConfig appConfig) throws W;
}
