package com.dansoftware.libraryapp.launcher;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.entry.EntryActivity;
import com.dansoftware.libraryapp.gui.mainview.MainActivity;
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
    private final DatabaseTracker databaseTracker;

    /**
     * Creates a basic ActivityLauncher with the {@link LauncherMode#INIT} mode.
     */
    public ActivityLauncher() {
        this(LauncherMode.INIT);
    }

    /**
     * Creates an ActivityLauncher with a custom {@link LauncherMode}.
     *
     * <p>
     * It uses the global {@link DatabaseTracker} object for launching the activities
     *
     * @param mode the "launcher-mode" that defines the behaviour
     * @see DatabaseTracker#getGlobal()
     */
    public ActivityLauncher(@NotNull LauncherMode mode) {
        this(mode, Collections.emptyList());
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode} and allows us to pass the application-arguments.
     *
     * <p>
     * It uses the global {@link DatabaseTracker} object for launching the activities.
     *
     * @param mode   the "launcher-mode" that defines the behaviour
     * @param params the program-arguments
     * @see ArgumentTransformer#transform(List)
     * @see DatabaseTracker#getGlobal()
     */
    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params));
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode} and with a {@link DatabaseMeta} object
     * that describes the database that the ActivityLauncher should launch.
     *
     * <p>
     * It uses the global {@link DatabaseTracker} object for launching the activities.
     *
     * @param mode         the "launcher-mode" that defines the behaviour
     * @param databaseMeta the database-meta object
     * @see DatabaseTracker#getGlobal()
     */
    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable DatabaseMeta databaseMeta) {
        this(mode, databaseMeta, DatabaseTracker.getGlobal());
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode}, custom {@link DatabaseTracker} and with a {@link DatabaseMeta} object
     * that describes the database that the ActivityLauncher should launch.
     *
     * @param mode         the "launcher-mode" that defines the behaviour
     * @param databaseMeta the database-meta object
     * @param tracker      the {@link DatabaseTracker} object that will be used by the launched activity for updating it's content
     */
    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable DatabaseMeta databaseMeta, @NotNull DatabaseTracker tracker) {
        this.mode = Objects.requireNonNull(mode, "The LauncherMode shouldn't be null");
        this.argument = databaseMeta;
        this.databaseTracker = tracker;
    }

    /**
     * Launches the right activity.
     */
    public void launch() {
        logger.debug("{} mode detected", mode);
        if (argument != null) {
            logger.debug("argument found");
            handleArgument(mode, argument);
        } else {
            logger.debug("no argument found");
            handleNoArgument(mode);
        }
    }

    /**
     * Handles the argument depending on the launcherMode.
     *
     * @param launcherMode the launcher-mode
     * @param argument the argument
     * @see #handleArgumentInit(DatabaseMeta)
     * @see #handleArgumentAlreadyRunning(DatabaseMeta)
     */
    private void handleArgument(@NotNull LauncherMode launcherMode,
                                @NotNull DatabaseMeta argument) {
        switch (launcherMode) {
            case INIT:
                handleArgumentInit(argument);
                break;
            case ALREADY_RUNNING:
                handleArgumentAlreadyRunning(argument);
                break;
        }
    }

    /**
     * Handles the argument assuming the launcherMode is {@link LauncherMode#INIT}.
     *
     * @param argument the application-argument
     */
    private void handleArgumentInit(DatabaseMeta argument) {
        //we add the launched database to the last databases
        logger.debug("adding the launched database into the LoginData...");
        LoginData loginData = getLoginData();
        if (!loginData.getSavedDatabases().contains(argument)) {
            loginData.getSavedDatabases().add(argument);
            onNewDatabaseAdded(argument);
            saveLoginData(loginData);
        }

        logger.debug("trying to sign in into the database...");
        Database database = NitriteDatabase.getAuthenticator()
                .onFailed((title, message, t) -> {
                    Platform.runLater(() -> {
                        LoginData temp = getLoginData();
                        //we select it, but we don't save it to the configurations
                        temp.setSelectedDatabase(argument);

                        EntryActivity entryActivity = showEntryActivity();
                        entryActivity.showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(entryActivity);
                    });
                }).auth(argument);

        //the login-process was successful
        if (database != null) {
            logger.debug("signed in into the argument-database successfully, launching a MainActivity...");
            Platform.runLater(() -> {
                onActivityLaunched(showMainActivity(database));
            });
        }

    }

    /**
     * Handles the argument assuming the launcherMode is {@link LauncherMode#ALREADY_RUNNING}.
     *
     * @param argument the application-argument
     */
    private void handleArgumentAlreadyRunning(DatabaseMeta argument) {
        //if there is an Activity opened with the database we focus on that,
        // otherwise we open a new activity for it
        MainActivity.getByDatabase(argument)
                .ifPresentOrElse(MainActivity::toFront, () -> handleArgumentInit(argument));
    }

    /**
     * Handles the situation when the application-argument not exists
     * depending on the launcher-mode.
     *
     * @param launcherMode the {@link LauncherMode}
     */
    private void handleNoArgument(LauncherMode launcherMode) {
        switch (launcherMode) {
            case INIT:
                handleNoArgumentInit();
                break;
            case ALREADY_RUNNING:
                handleNoArgumentAlreadyRunning();
                break;
        }
    }

    /**
     * Handles the situation when the application-argument not exists
     * assuming that the launcher-mode is {@link LauncherMode#INIT}
     */
    private void handleNoArgumentInit() {
        //if there was no application-argument
        //it is basically a normal application-start.
        if (getLoginData().autoLoginTurnedOn()) {
            //if auto login is turned on
            logger.debug("auto login is turned on, trying to sign in into the database...");
            autoLogin();
        } else {
            //if auto login is turned off
            logger.debug("auto-login is turned off, launching a basic EntryActivity...");
            Platform.runLater(() -> onActivityLaunched(showEntryActivity()));
        }

    }

    /**
     * Handles the situation when the application-argument not exists
     * assuming that the launcher-mode is {@link LauncherMode#ALREADY_RUNNING}
     */
    private void handleNoArgumentAlreadyRunning() {
        //no argument
        //just focusing on a random window
        logger.debug("no argument found, focusing on a random window...");

        Platform.runLater(() -> {
            EntryActivity.getShowingEntries()
                    .stream()
                    .limit(1)
                    .findAny()
                    .ifPresent(EntryActivity::toFront);
        });
    }

    /**
     * Handles the situation when auto-login is turned on
     */
    private void autoLogin() {
        Database database = NitriteDatabase.getAuthenticator()
                .onFailed((title, message, t) -> {
                    logger.debug("failed signing into the database");
                    Platform.runLater(() -> {
                        EntryActivity entryActivity = showEntryActivity();
                        entryActivity.showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(entryActivity);
                    });
                }).auth(getLoginData().getAutoLoginDatabase(), getLoginData().getAutoLoginCredentials());

        //the login-process was successful
        if (database != null) {
            logger.debug("signed in into the auto-login database successfully, launching a MainActivity...");

            Platform.runLater(() -> {
                onActivityLaunched(showMainActivity(database));
            });
        }
    }

    private EntryActivity showEntryActivity() {
        EntryActivity entryActivity = newBasicEntryActivity();
        entryActivity.show();
        return entryActivity;
    }

    private MainActivity showMainActivity(Database database) {
        MainActivity mainActivity = new MainActivity(database);
        mainActivity.show();
        return mainActivity;
    }

    private EntryActivity newBasicEntryActivity() {
        return new EntryActivity(getLoginData(), databaseTracker);
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
     * <p>
     * The base method adds the {@link DatabaseMeta} object to the {@link DatabaseTracker}
     * given to the {@link ActivityLauncher}.
     *
     * @param databaseMeta the meta-information of the database
     */
    protected void onNewDatabaseAdded(DatabaseMeta databaseMeta) {
        databaseTracker.addDatabase(databaseMeta);
    }

    /**
     * Called on the UI-thread, when an "Activity" is launched
     *
     * @param context the 'activity' through the {@link Context} interface
     */
    protected abstract void onActivityLaunched(Context context);

}
