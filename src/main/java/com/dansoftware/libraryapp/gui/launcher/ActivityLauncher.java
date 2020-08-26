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

/**
 * An ActivityLauncher can launch the right "activity" ({@link EntryActivity}, {@link MainActivity}) depending
 * on the program-arguments and other factors.
 *
 * <p>
 * The base {@link ActivityLauncher} is an abstract structure because, for example it needs a method
 * how to retrieve the {@link LoginData} or how to save it and so on.
 *
 * <p>
 * An {@link ActivityLauncher} behaves differently in different modes called {@link LauncherMode}s.
 *
 * <p>
 * An {@link ActivityLauncher} might do work that doesn't need the UI thread, so it also implements
 * the {@link Runnable} interface to make it easy to use it with a background {@link Thread}.
 *
 * <pre>{@code
 * ActivityLauncher activityLauncher = ...;
 * new Thread(activityLauncher).start();
 * }</pre>
 * <p>
 * If a task that the {@link ActivityLauncher} preforms needs the UI thread, it will run that
 * on the javaFX application thread (using {@link Platform#runLater(Runnable)}).
 *
 * @author Daniel Gyorffy
 * @see LauncherMode
 */
public abstract class ActivityLauncher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLauncher.class);

    private final LauncherMode mode;
    private final DatabaseMeta argument;

    /**
     * Creates a basic ActivityLauncher with the {@link LauncherMode#INIT} mode.
     */
    public ActivityLauncher() {
        this(LauncherMode.INIT);
    }

    /**
     * Creates an ActivityLauncher with a custom {@link LauncherMode}.
     *
     * @param mode the "launcher-mode" that defines the behaviour
     */
    public ActivityLauncher(@NotNull LauncherMode mode) {
        this(mode, Collections.emptyList());
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode} and allows us to pass the application-arguments.
     *
     * @param mode   the "launcher-mode" that defines the behaviour
     * @param params the program-arguments
     * @see ArgumentTransformer#transform(List)
     */
    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params));
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode} and with a {@link DatabaseMeta} object
     * that describes the database that the ActivityLauncher should launch.
     *
     * @param mode         the "launcher-mode" that defines the behaviour
     * @param databaseMeta the database-meta object
     */
    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable DatabaseMeta databaseMeta) {
        this.mode = Objects.requireNonNull(mode, "The LauncherMode shouldn't be null");
        this.argument = databaseMeta;
    }

    /**
     * Launches the right activity.
     */
    public void launch() {

        switch (mode) {
            case INIT:
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
                break;
            case ALREADY_RUNNING:
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
                break;
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
