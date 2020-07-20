package com.dansoftware.libraryapp.config;

/**
 * An AppConfigReader can read configurations into an {@link AppConfig} object from a particular
 * data-source.
 *
 * <p>
 * An AppConfigReader also an {@link AutoCloseable} that means that it should be closed after we
 * read the data.
 *
 * @param <R> the exception-type that can occur in the {@link #read()} method
 * @param <C> the exception-type that can occur in the {@link #close()} method
 */
public interface AppConfigReader<R extends Exception, C extends Exception> extends AutoCloseable {
    AppConfig read() throws R;

    @Override
    void close() throws C;
}
