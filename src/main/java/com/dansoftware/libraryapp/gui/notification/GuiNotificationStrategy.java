package com.dansoftware.libraryapp.gui.notification;

import com.dansoftware.libraryapp.gui.util.Alerts;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.Main;
import javafx.application.Platform;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

/**
 * A GuiNotificationStrategy is a NotificationStrategy that displays the
 * notifications on the GUI.
 *
 * <p>
 *
 * @see Notification
 */
@Deprecated
public class GuiNotificationStrategy implements NotificationStrategy {
    @Override
    public void handle(Notification notification) {
        Main.runAfterStart(() -> Platform.runLater(() -> {
            var notificationBuilder = Notifications.create()
                    .hideAfter(notification.getVisibilityDuration())
                    .position(Pos.BOTTOM_RIGHT)
                    .title(notification.getTitle())
                    .text(notification.getMsg());

            if (notification.getEventHandler() != null)
                notificationBuilder.onAction(notification.getEventHandler());
            else if (notification.getThrowable() != null)
                //notificationBuilder.onAction(e -> Alerts.showErrorAlertDialog(notification.getThrowable()));

            //
            //getPrimaryStage().ifPresent(notificationBuilder::owner);

            themeSettings(notificationBuilder);
            show(notification, notificationBuilder);
        }));
    }

    private void themeSettings(Notifications notificationBuilder) {
        if (Theme.getDefault() == Theme.DARK)
            notificationBuilder.darkStyle();
    }

    private void show(Notification notification, Notifications notificationBuilder) {
        switch (notification.getLevel()) {
            case INFO:
                notificationBuilder.showInformation();
                break;
            case WARNING:
                notificationBuilder.showWarning();
                break;
            case ERROR:
                notificationBuilder.showError();
                break;
            default:
                notificationBuilder.show();
                break;
        }
    }
}
