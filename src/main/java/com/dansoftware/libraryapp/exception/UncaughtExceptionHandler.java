package com.dansoftware.libraryapp.exception;

import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Notification.create()
                .level(NotificationLevel.ERROR)
                .title("error.occurred")
                .msg("error.details")
                .cause(e)
                .show();
    }
}
