package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.util.Alerts;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;

import java.util.Optional;
import java.util.logging.*;

import static com.dansoftware.libraryapp.main.Main.getPrimaryStage;

/**
 * Logging handler for displaying info and warning logs on the gui
 */
public class GuiHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        if (record instanceof GuiLog) {

            Notifications notificationBuilder = Notifications.create()
                    .text(record.getMessage())
                    .onAction(event -> Optional.ofNullable(record.getThrown()).ifPresent(Alerts::showErrorAlertDialog))
                    .owner(getPrimaryStage())
                    .position(Pos.BOTTOM_RIGHT);

            if (record.getLevel() == Level.INFO)
                notificationBuilder
                        .title("Info")
                        .showInformation();

            else if (record.getLevel() == Level.WARNING)
                notificationBuilder
                        .title("Warning")
                        .showWarning();

        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
