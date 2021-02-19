package com.dansoftware.boomega.db.auth;

/**
 * Used by {@link DatabaseAuthenticator}s
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
