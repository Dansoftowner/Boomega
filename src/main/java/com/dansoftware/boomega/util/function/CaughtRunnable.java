package com.dansoftware.boomega.util.function;

/**
 * A {@link CaughtRunnable} is a {@link Runnable}
 * that handles checked exceptions, and doesn't need
 * exception-handling.
 * <p>
 * Example:
 * <pre>{@code
 * //error, the Runnable.run() method does not allow to throw checked exceptions
 * Runnable runnableExample = () -> { throw new Exception(); };
 *
 * -------
 *
 * CaughtRunnable caughtRunnableExample = () -> { throw new Exception(); }; //OK
 * caughtRunnableExample.run(); // throws a RuntimeException
 * }</pre>
 *
 * @author Daniel Gyorffy
 */
public interface CaughtRunnable extends Runnable {

    void exceptionRun() throws Exception;

    @Override
    default void run() {
        try {
            exceptionRun();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
