package com.dansoftware.libraryapp.util.function;

@FunctionalInterface
public interface UncaughtSupplier<T, E extends Exception> {
    T get() throws E;
}
