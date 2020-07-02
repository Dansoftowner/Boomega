package com.dansoftware.libraryapp.util;


public interface CustomCloseable<E extends Exception> extends AutoCloseable {
    void close() throws E;
}
