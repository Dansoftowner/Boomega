package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.entry.login.data.LoginData;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;
import javafx.application.Platform;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InstanceService is responsible for listening to application instances
 * and avoiding them if there is an already running instance of the application.
 * It also forwards the application arguments to the already running instance.
 *
 * <p>
 * The {@link #open(String[])} method should be used for starting the 'service', and it
 * should be called immediately after the program starts.
 *
 * @author Daniel Gyorffy
 */
public class InstanceService implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private static final String APPLICATION_ID = "com.dansoftware.libraryapp";

    private static InstanceService instance;

    private InstanceService(String[] args) {
        try {
            JUnique.acquireLock(APPLICATION_ID, this);
        } catch (AlreadyLockedException e) {
            logger.info("An application is already running with the id: '" + APPLICATION_ID + "'");
            logger.info("Sending the arguments to the already running instance...");

            if (ArrayUtils.isEmpty(args)) {
                JUnique.sendMessage(APPLICATION_ID, StringUtils.EMPTY);
            } else {
                JUnique.sendMessage(APPLICATION_ID, args[0]);
            }

            logger.info("Exiting...");
            System.exit(0);
        }
    }

    @Override
    public String handle(String arg) {
        logger.debug("Message from another process: {}", arg);

        if (StringUtils.isEmpty(arg)) {
            //focusing on some window
            Platform.runLater(() -> {
                AppEntry.getShowingEntries()
                        .stream()
                        .limit(1)
                        .findAny()
                        .ifPresent(AppEntry::requestFocus);
            });
        } else {
            //if the file that the argument represents is already opened
            //with the application, we focus on that window, otherwise, we just
            //open the AppEntry with it

            DatabaseMeta databaseMeta = DatabaseMeta.parseFrom(arg);
            /*AppEntry.getOpenedDatabases()
                    .stream()
                    .filter(databaseMeta::equals)
                    .findAny()
                    .ifPresentOrElse(db -> {
                        Platform.runLater(() -> AppEntry.getEntryOf(db).ifPresent(AppEntry::requestFocus));
                    }, () -> {
                        Preferences.getPreferences().editor().modify(Preferences.Key.LOGIN_DATA, loginData -> {
                            loginData.setSelectedDatabase(databaseMeta);
                        }).tryCommit();

                        LoginData loginData = Preferences.getPreferences().get(Preferences.Key.LOGIN_DATA);
                        //loginData.getLastDatabases().removeAll(AppEntry.getOpenedDatabases());
                        Platform.runLater(() -> new AppEntry(loginData).show());
                    });*/

        }
        return null;
    }

    public static void open(String[] args) {
        if (instance == null) {
            instance = new InstanceService(args);
        }
    }

}
