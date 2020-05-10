package com.dansoftware.libraryapp.util.function;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike the {@link java.util.function.Consumer} it can throw an {@link Exception}.
 *
 * @param <T> the type of the input to the operation
 * @param <E> the type of the exception
 */
public interface UnhandledConsumer<T, E extends Exception> {
    void accept(T object) throws E;
}
