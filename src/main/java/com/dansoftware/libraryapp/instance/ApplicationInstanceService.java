package com.dansoftware.libraryapp.instance;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.launcher.ActivityLauncher;
import com.dansoftware.libraryapp.launcher.LauncherMode;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for listening to application instances
 * and avoiding them if there is an already running instance of the application.
 * It also forwards the application arguments to the already running instance.
 *
 * <p>
 * The {@link #open(String[])} method should be used for starting the 'service', and it
 * should be called immediately after the program starts.
 *
 * @author Daniel Gyorffy
 */
public class ApplicationInstanceService implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInstanceService.class);

    private static final String APPLICATION_ID = "com.dansoftware.libraryapp";
    private static ApplicationInstanceService instance;

    private static final String SUCCESS_MESSAGE = "OK";
    private static final String ERROR_MESSAGE = "FAILED";

    private ApplicationInstanceService(String[] args) {
        try {
            JUnique.acquireLock(APPLICATION_ID, this);
        } catch (AlreadyLockedException e) {
            logger.info("An application is already running with the id: '{}'", e.getID());
            logger.info("Sending the arguments to the already running instance...");
            logger.debug("The argument(s) are: {}", new Object() {
                public String toString() {
                    return Arrays.toString(args);
                }
            });

            try {
                writeArgumentsToFile(RuntimeArgumentsHolderFile.INSTANCE, args);
                JUnique.sendMessage(APPLICATION_ID, SUCCESS_MESSAGE);
            } catch (IOException ioException) {
                JUnique.sendMessage(APPLICATION_ID, ERROR_MESSAGE);
            }

            logger.info("Exiting...");
            System.exit(0);
        }
    }

    private void writeArgumentsToFile(File dest, String[] args) throws IOException {
        try (var fileWriter = new BufferedWriter(new FileWriter(dest))) {
            fileWriter.write(String.join("\n", args));
        }
    }

    private List<String> readArgumentsFromFile(File src) throws IOException {
        List<String> lines;
        try (var reader = new BufferedReader(new FileReader(src))) {
            lines = reader.lines().collect(Collectors.toList());
        }

        return lines;
    }

    @Override
    public String handle(String arg) {
        logger.debug("message from another process: {}", arg);
        logger.debug("Reading arguments from file...");
        try {
            List<String> args = readArgumentsFromFile(RuntimeArgumentsHolderFile.INSTANCE);
            logger.debug("starting an ActivityLauncher...");
            new ActivityLauncherImpl(args, Preferences.getPreferences(), DatabaseTracker.getGlobal()).launch();
            logger.debug("Clearing argument-holder file...");
            RuntimeArgumentsHolderFile.INSTANCE.clear();
        } catch (IOException e) {
            logger.error("Failed to read the arguments from file!", e);
        }
        return null;
    }

    public static synchronized void open(String[] args) {
        if (instance == null) {
            instance = new ApplicationInstanceService(args);
        }
    }

    private static class ActivityLauncherImpl extends ActivityLauncher {

        private final LoginData loginData;

        public ActivityLauncherImpl(@Nullable List<String> args,
                                    @NotNull Preferences preferences,
                                    @NotNull DatabaseTracker databaseTracker) {
            super(LauncherMode.ALREADY_RUNNING, preferences, databaseTracker, args);
            this.loginData = buildLoginData();
        }

        private LoginData buildLoginData() {
            //removing all already opened databases from the LoginData
            Set<DatabaseMeta> databaseUsing = DatabaseTracker.getGlobal().getUsingDatabases();
            LoginData loginData = getPreferences().get(Preferences.Key.LOGIN_DATA);
            loginData.getSavedDatabases().removeAll(databaseUsing);
            loginData.setSelectedDatabase(null);
            loginData.setAutoLoginDatabase(null);

            return loginData;
        }

        @Override
        protected LoginData getLoginData() {
            return loginData;
        }

        @Override
        protected void saveLoginData(LoginData loginData) {
            getPreferences()
                    .editor()
                    .put(Preferences.Key.LOGIN_DATA, loginData)
                    .tryCommit();
        }

        @Override
        protected void onActivityLaunched(Context context) {
            //displaying message '$database launched'
        }
    }

}
