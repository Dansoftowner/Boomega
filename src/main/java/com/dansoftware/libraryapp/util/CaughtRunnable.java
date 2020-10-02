package com.dansoftware.libraryapp.util;

public interface CaughtRunnable extends Runnable {

    void exceptionRun() throws Exception;

    @Override
    default void run() {
        try {
            exceptionRun();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
