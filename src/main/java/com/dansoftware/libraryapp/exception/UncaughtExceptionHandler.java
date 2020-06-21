package com.dansoftware.libraryapp.exception;

import com.dansoftware.libraryapp.gui.util.ExceptionHandler;
import org.controlsfx.control.Notifications;

import static com.dansoftware.libraryapp.locale.Bundles.getNotificationMsg;

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
