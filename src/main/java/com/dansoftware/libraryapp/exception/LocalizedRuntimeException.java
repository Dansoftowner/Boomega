package com.dansoftware.libraryapp.exception;

import static com.dansoftware.libraryapp.util.Bundles.getExceptionBundle;

/**
 * This exception-type can be used to throw runtime exceptions with localized
 * messages from the resource bundle
 */
public class LocalizedRuntimeException extends RuntimeException {

    public LocalizedRuntimeException(String message, Throwable cause) {
        super(getExceptionBundle().getString(message), cause);
    }
}
