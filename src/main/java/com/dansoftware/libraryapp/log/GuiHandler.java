package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.gui.util.Alerts;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;

import java.util.Optional;
import java.util.logging.*;

import static com.dansoftware.libraryapp.main.GuiApplicationStarter.getPrimaryStage;
import static com.dansoftware.libraryapp.util.Bundles.*;

/**
 * Logging handler for displaying info and warning logs on the gui
 * This handler is compatible only with the {@link GuiLog} logrecord,
 * so if the LogRecord is not a GuiLog, it will do nothing
 *
 * @see LogRecord
 * @see GuiLog
 * @author Daniel Gyorffy
 */
public class GuiHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        if (record instanceof GuiLog) {

            Optional<Throwable> throwableOptional = Optional.ofNullable(record.getThrown());
            Optional<Duration> durationOptional = Optional.ofNullable(asGuiLog(record).getHideAfterDuration());

            var notificationBuilder = Notifications.create()
                    .text(record.getMessage())
                    .hideAfter(durationOptional.orElse(Duration.INDEFINITE))
                    .onAction(event -> throwableOptional.ifPresent(Alerts::showErrorAlertDialog))
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
