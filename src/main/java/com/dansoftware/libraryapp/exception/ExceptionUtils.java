package com.dansoftware.libraryapp.exception;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class contains some utilities for exception handling
 */
public class ExceptionUtils {

    /**
     * Don't let anyone to create an instance of this class
     */
    private ExceptionUtils() {
    }

    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (var thread, var exception) -> {

    };


    public static Thread.UncaughtExceptionHandler getExceptionHandler() {
        return EXCEPTION_HANDLER;
    }

    public static ResourceBundle getExceptionResourceBundle() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.ExceptionBundle");
    }
}
