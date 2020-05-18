package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataBaseFileRecognizer;
import com.dansoftware.libraryapp.gui.notification.MessageBuilder;
import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A DefaultDBConnection is a {@link SQLiteConnection} that defines
 * the concrete behaviour of the application's database handling.
 *
 * <p>
 * A DefaultDBConnection object handles the sql exception that may
 * occurs during the connection-creation: logs
 * it and show a user-notification about it.
 *
 * @author Daniel Gyorffy
 */
public final class DefaultDBConnection extends SQLiteConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDBConnection.class);
    private static File databaseFile;

    public DefaultDBConnection() {
        super(databaseFile = new DataBaseFileRecognizer().getDBFile(), exception -> {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Couldn't create database connection with sqlite file: " + databaseFile, exception);

            //create the user notification
            Notification.create()
                    .level(NotificationLevel.ERROR)
                    .cause(exception)
                    .title("db.connection.failed.title")
                    .msg(new MessageBuilder()
                            .msg("db.connection.failed")
                            .args(new Object[]{databaseFile.getName()}))
                    .show();
        });
    }
}
