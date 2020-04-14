package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.util.Alerts;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;

import java.util.Optional;
import java.util.logging.*;

import static com.dansoftware.libraryapp.main.Main.getPrimaryStage;
import static com.dansoftware.libraryapp.util.Bundles.*;

/**
 * Logging handler for displaying info and warning logs on the gui
 */
public class GuiHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        if (record instanceof GuiLog) {

            Notifications notificationBuilder = Notifications.create()
                    .text(record.getMessage())
                    .hideAfter(Duration.INDEFINITE)
                    .onAction(event -> Optional.ofNullable(record.getThrown()).ifPresent(Alerts::showErrorAlertDialog))
                    .owner(getPrimaryStage())
                    .position(Pos.BOTTOM_RIGHT);

            if (record.getLevel() == Level.SEVERE)
                notificationBuilder
                        .title(getCommonBundle().getString("notifications.error.occured"))
                        .showError();

            else if (record.getLevel() == Level.INFO)
                notificationBuilder
                        .title(getCommonBundle().getString("notifications.info"))
                        .showInformation();

            else if (record.getLevel() == Level.WARNING)
                notificationBuilder
                        .title(getCommonBundle().getString("notification.warning"))
                        .showWarning();


            asGuiLog(record).getTitle().ifPresent(notificationBuilder::title);
        }
    }

    private GuiLog asGuiLog(LogRecord logRecord) {
        return (GuiLog) logRecord;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
