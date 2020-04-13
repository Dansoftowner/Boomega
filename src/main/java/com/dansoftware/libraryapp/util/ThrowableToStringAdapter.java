package com.dansoftware.libraryapp.util;

import java.io.*;

/**
 * This class can convert the exception stack trace
 * to a string. To get the string of the exception stack trace
 * you have to call the {@link ThrowableToStringAdapter#toString()} method.
 *
 * @author Daniel Gyorffy
 */
public class ThrowableToStringAdapter {

    private Throwable cause;

    public ThrowableToStringAdapter(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);

        try (stringWriter; printStream) {
            cause.printStackTrace(printStream);
            return stringWriter.toString();
        } catch (IOException e) {
            return null;
        }
    }

}
