package com.dansoftware.libraryapp.gui.notification;

public class DummyNotificationStrategy implements NotificationStrategy{

    private Notification notification;

    @Override
    public void handle(Notification notification) {
        this.notification = notification;
    }

    public Notification getHandledNotification() {
        return notification;
    }

}
