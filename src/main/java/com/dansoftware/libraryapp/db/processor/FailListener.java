package com.dansoftware.libraryapp.db.processor;

/**
 * Used by {@link LoginProcessor}s, more precisely by {@link DatabaseFactory} objects
 * for handling the exceptions.
 *
 * @author Daniel Gyorffy
 */
public interface FailListener {

    /**
     * Returns a listener that doesn't do anything.
     *
     * @return the "empty" {@link FailListener}
     */
    static FailListener empty() {
        return (title, message, t) -> {
        };
    }

    default void onFail(String title, String message) {
        onFail(title, message, null);
    }

    void onFail(String title, String message, Throwable t);
}
