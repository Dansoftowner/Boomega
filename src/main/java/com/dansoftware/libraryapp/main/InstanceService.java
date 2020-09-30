package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.launcher.ActivityLauncher;
import com.dansoftware.libraryapp.launcher.LauncherMode;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

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
            logger.info("An application is already running with the id: '{}'", e.getID());
            logger.info("Sending the arguments to the already running instance...");

            JUnique.sendMessage(APPLICATION_ID, ArrayUtils.isEmpty(args) ? StringUtils.EMPTY : args[0]);

            logger.info("Exiting...");
            System.exit(0);
        }
    }

    @Override
    public String handle(String arg) {
        logger.debug("message from another process: {}", arg);
        logger.debug("starting an ActivityLauncher...");
        new ActivityLauncherImpl(arg).launch();
        return null;
    }

    public static void open(String[] args) {
        if (instance == null) {
            instance = new InstanceService(args);
        }
    }

    private static class ActivityLauncherImpl extends ActivityLauncher {

        private final LoginData loginData;

        public ActivityLauncherImpl(@Nullable String arg) {
            super(LauncherMode.ALREADY_RUNNING, Collections.singletonList(arg));
            this.loginData = buildLoginData();
        }

        private LoginData buildLoginData() {
            //removing all already opened databases from the LoginData
            Set<DatabaseMeta> databaseUsing = DatabaseTracker.getGlobal().getUsingDatabases();
            LoginData loginData = Preferences.getPreferences().get(Preferences.Key.LOGIN_DATA);
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
            Preferences.getPreferences()
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
