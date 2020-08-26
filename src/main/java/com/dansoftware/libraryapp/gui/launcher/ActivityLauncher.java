package com.dansoftware.libraryapp.gui.launcher;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.entry.EntryActivity;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.login.data.LoginData;
import com.dansoftware.libraryapp.gui.entry.mainview.MainActivity;
import com.dansoftware.libraryapp.main.ArgumentTransformer;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ActivityLauncher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLauncher.class);

    private final LauncherMode mode;
    private final DatabaseMeta argument;

    public ActivityLauncher() {
        this(LauncherMode.INIT);
    }

    public ActivityLauncher(@NotNull LauncherMode mode) {
        this(mode, Collections.emptyList());
    }

    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params));
    }

    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable DatabaseMeta databaseMeta) {
        this.mode = Objects.requireNonNull(mode, "The LauncherMode shouldn't be null");
        this.argument = databaseMeta;
    }

    public void launch() {

        if (mode == LauncherMode.INIT) {
            //if we are in INIT mode
            logger.debug("INIT mode detected");

            if (argument == null) {
                //if there was no application-argument
                logger.debug("no argument found");

                if (getLoginData().autoLoginTurnedOn()) {
                    //if auto login is turned on
                    logger.debug("auto login is turned on, trying to sign in into the database...");

                    Database database = LoginProcessor.of(NitriteDatabase.factory())
                            .onFailed((title, message, t) -> {
                                logger.debug("failed signing into the database");
                                Platform.runLater(() -> {
                                    var appEntry = new EntryActivity(getLoginData());
                                    appEntry.show();
                                    appEntry.showErrorDialog(title, message, (Exception) t);
                                    onActivityLaunched(appEntry);
                                });
                            }).process(getLoginData().getAutoLoginDatabase(), getLoginData().getAutoLoginCredentials());

                    //the login-process was successful
                    if (database != null) {
                        logger.debug("signed in into the auto-login database successfully, launching a MainActivity...");

                        Platform.runLater(() -> {
                            MainActivity mainActivity = new MainActivity(database);
                            mainActivity.show();
                            onActivityLaunched(mainActivity);
                        });
                    }

                } else {
                    //if auto login is turned off
                    logger.debug("auto-login is turned off, launching a basic EntryActivity...");

                    Platform.runLater(() -> {
                        EntryActivity entryActivity = new EntryActivity(getLoginData());
                        entryActivity.show();
                        onActivityLaunched(entryActivity);
                    });
                }

            } else {
                //if there was application-argument
                logger.debug("argument found");
                handleArgument();
            }

        } else if (mode == LauncherMode.ALREADY_RUNNING) {
            logger.debug("ALREADY_RUNNING mode detected");

            if (argument == null) {
                //no argument
                //just focusing on a random window
                logger.debug("no argument found, focusing on a random window...");

                Platform.runLater(() -> {
                    EntryActivity.getShowingEntries()
                            .stream()
                            .limit(1)
                            .findAny()
                            .ifPresent(EntryActivity::requestFocus);
                });
            } else {
                //there is argument
                logger.debug("argument found, trying to focus on an already running MainActivity or creating a new one");

                //if there is an Activity opened with the database we focus on that,
                // otherwise we open a new activity for it
                MainActivity.getByDatabase(argument)
                        .ifPresentOrElse(MainActivity::requestFocus, this::handleArgument);
            }
        }
    }

    private void handleArgument() {
        //we add the launched database to the last databases
        logger.debug("adding the launched database into the LoginData...");
        LoginData loginData = getLoginData();
        if (!loginData.getLastDatabases().contains(argument)) {
            loginData.getLastDatabases().add(argument);
            onNewDatabaseAdded(argument);
            saveLoginData(loginData);
        }

        logger.debug("trying to sign in into the database...");
        Database database = LoginProcessor.of(NitriteDatabase.factory())
                .onFailed((title, message, t) -> {
                    Platform.runLater(() -> {
                        LoginData temp = getLoginData();
                        //we select it, but we don't save it to the configurations
                        temp.setSelectedDatabase(argument);

                        var appEntry = new EntryActivity(temp);
                        appEntry.show();
                        appEntry.showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(appEntry);
                    });
                }).process(argument);

        //the login-process was successful
        if (database != null) {
            logger.debug("signed in into the argument-database successfully, launching a MainActivity...");
            Platform.runLater(() -> {
                MainActivity mainActivity = new MainActivity(database);
                mainActivity.show();
                onActivityLaunched(mainActivity);
            });
        }

    }

    @Override
    public void run() {
        launch();
    }

    /**
     * Defines how to get the {@link LoginData} for the base {@link ActivityLauncher} object.
     *
     * @return the {@link LoginData} object.
     */
    protected abstract LoginData getLoginData();

    /**
     * Defines how to save the {@link LoginData} for the base {@link ActivityLauncher} object.
     */
    protected abstract void saveLoginData(LoginData loginData);

    /**
     * Called, when a new database (from the arguments) is added to the login-data.
     *
     * @param databaseMeta the meta-information of the database
     */
    protected abstract void onNewDatabaseAdded(DatabaseMeta databaseMeta);

    /**
     * Called on the UI-thread, when an "Activity" is launched
     *
     * @param context the 'activity' through the {@link Context} interface
     */
    protected abstract void onActivityLaunched(Context context);

}
