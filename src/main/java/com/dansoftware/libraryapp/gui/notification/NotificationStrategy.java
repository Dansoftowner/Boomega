package com.dansoftware.libraryapp.gui.notification;

/**
 * A NotificationStrategy can handle Notifications in a particular way.
 *
 * <p>
 *
 * @see Notification
 */
public interface NotificationStrategy {
    void handle(Notification notification);
}
