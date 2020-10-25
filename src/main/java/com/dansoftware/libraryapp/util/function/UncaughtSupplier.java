package com.dansoftware.libraryapp.util.function;

/**
 * An {@link UncaughtSupplier} is like a {@link java.util.function.Supplier} that
 * can throw an exception
 *
 * @param <T> the type of the object that the {@link UncaughtSupplier} supplies
 * @param <E> the type of the {@link Exception} that will the {@link UncaughtSupplier} throw
 * @author Daniel Gyorffy
 */
@FunctionalInterface
public interface UncaughtSupplier<T, E extends Exception> {
    T get() throws E;
}
