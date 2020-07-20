package com.dansoftware.libraryapp.config.write;

import com.dansoftware.libraryapp.config.AppConfig;

/**
 * An AppConfigWriter can write configurations from an {@link AppConfig} object to a particular
 * target.
 *
 * <p>
 * An AppConfigWriter also an {@link AutoCloseable} that means that it should be closed after we
 * write the data.
 *
 * @param <W> the exception-type that can occur in the {@link #write(AppConfig)} ()} method
 * @param <C> the exception-type that can occur in the {@link #close()} method
 */
public interface AppConfigWriter<W extends Exception, C extends Exception> extends AutoCloseable {
    void write(AppConfig appConfig) throws W;

    @Override
    void close() throws C;
}
