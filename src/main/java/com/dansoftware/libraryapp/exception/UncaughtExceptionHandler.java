package com.dansoftware.libraryapp.exception;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        /*Notifications.create()
                .title(getNotificationMsg("error.occured"))
                .text("error.details")
                .onAction(new ExceptionHandler<>(e))
                .showError();*/
    }
}
