package com.dansoftware.boomega.launcher;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.logindata.LoginData;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.db.NitriteDatabase;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.entry.EntryActivity;
import com.dansoftware.boomega.gui.login.DatabaseLoginListener;
import com.dansoftware.boomega.gui.login.LoginActivity;
import com.dansoftware.boomega.gui.login.quick.QuickLoginActivity;
import com.dansoftware.boomega.gui.mainview.MainActivity;
import com.dansoftware.boomega.main.ArgumentTransformer;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
public class ActivityLauncher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLauncher.class);

    private final LauncherMode mode;
    private final DatabaseMeta argument;
    private final Preferences preferences;
    private final DatabaseTracker databaseTracker;
    private PostLaunchQueue postLaunchQueue;

    /**
     * Creates a basic ActivityLauncher with the {@link LauncherMode#INIT} mode.
     */
    public ActivityLauncher(@NotNull Preferences preferences, @NotNull DatabaseTracker databaseTracker) {
        this(LauncherMode.INIT, preferences, databaseTracker);
    }

    /**
     * Creates an ActivityLauncher with a custom {@link LauncherMode}.
     *
     * @param mode            the "launcher-mode" that defines the behaviour
     * @param preferences     the {@link Preferences} object that stores the application configurations.
     * @param databaseTracker the {@link DatabaseTracker} object that tracks the opened databases.
     * @see DatabaseTracker#getGlobal()
     */
    public ActivityLauncher(@NotNull LauncherMode mode,
                            @NotNull Preferences preferences,
                            @NotNull DatabaseTracker databaseTracker) {
        this(mode, preferences, databaseTracker, Collections.emptyList());
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode} and allows us to pass the application-arguments.
     *
     * @param mode            the "launcher-mode" that defines the behaviour
     * @param preferences     the {@link Preferences} object that stores the application configurations.
     * @param databaseTracker the {@link DatabaseTracker} object that tracks the opened databases.
     * @param params          the program-arguments
     * @see ArgumentTransformer#transform(List)
     * @see DatabaseTracker#getGlobal()
     */
    public ActivityLauncher(@NotNull LauncherMode mode,
                            @NotNull Preferences preferences,
                            @NotNull DatabaseTracker databaseTracker,
                            @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params), preferences, databaseTracker);
    }

    public ActivityLauncher(@NotNull LauncherMode mode,
                            @NotNull Preferences preferences,
                            @NotNull DatabaseTracker databaseTracker,
                            @Nullable PostLaunchQueue postLaunchQueue,
                            @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params), preferences, databaseTracker);
        this.postLaunchQueue = postLaunchQueue;
    }

    /**
     * Creates an ActivityLauncher with custom {@link LauncherMode}, custom {@link DatabaseTracker} and with a {@link DatabaseMeta} object
     * that describes the database that the ActivityLauncher should launch.
     *
     * @param mode         the "launcher-mode" that defines the behaviour
     * @param databaseMeta the database-meta object
     * @param tracker      the {@link DatabaseTracker} object that will be used by the launched activity for updating it's content
     */
    public ActivityLauncher(@NotNull LauncherMode mode,
                            @Nullable DatabaseMeta databaseMeta,
                            @NotNull Preferences preferences,
                            @NotNull DatabaseTracker tracker) {
        this.mode = Objects.requireNonNull(mode, "The LauncherMode shouldn't be null");
        this.argument = databaseMeta;
        this.preferences = preferences;
        this.databaseTracker = tracker;
    }

    protected Preferences getPreferences() {
        return preferences;
    }

    protected DatabaseTracker getDatabaseTracker() {
        return databaseTracker;
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
     * @param argument     the argument
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
            case INTERNAL:
                handleArgumentInternal(argument);
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
        onNewDatabaseAdded(argument);
        logger.debug("trying to sign in into the database...");
        Database database = NitriteDatabase.getAuthenticator()
                .onFailed((title, message, t) -> {
                    Platform.runLater(() -> {
                        LoginData temp = getLoginData();
                        //we select it, but we don't save it to the configurations
                        temp.setSelectedDatabase(argument);

                        EntryActivity entryActivity = showEntryActivity();
                        entryActivity.getContext().showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(entryActivity.getContext(), null);
                    });
                }).auth(argument);

        //the login-process was successful
        if (database != null) {
            logger.debug("signed in into the argument-database successfully, launching a MainActivity...");
            Platform.runLater(() -> {
                onActivityLaunched(showMainActivity(database).getContext(), argument);
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
                .map(MainActivity::getContext)
                .ifPresentOrElse(context -> Platform.runLater(context::toFront), () -> {
                    handleArgumentInit(argument);
                });
    }

    private void handleArgumentInternal(DatabaseMeta argument) {
        MainActivity.getByDatabase(argument)
                .map(MainActivity::getContext)
                .ifPresentOrElse(context -> Platform.runLater(context::toFront), () -> {
                    onNewDatabaseAdded(argument);
                    final DatabaseLoginListener onDatabaseLogin = db -> onActivityLaunched(showMainActivity(db).getContext(), argument);
                    Database database = NitriteDatabase.getAuthenticator()
                            .onFailed((title, message, t) -> {
                                Platform.runLater(() -> {
                                    onActivityLaunched(showQuickLoginActivity(argument, onDatabaseLogin).getContext(), argument);
                                });
                            }).auth(argument);

                    if (database != null) {
                        logger.debug("Signed into the argument-database successfully, launching a MainActivity...");
                        Platform.runLater(() -> {
                            onDatabaseLogin.onDatabaseOpened(database);
                        });
                    }
                });
    }

    /**
     * Handles the situation when the application-argument not exists
     * depending on the launcher-mode.
     *
     * @param launcherMode the {@link LauncherMode}
     */
    private void handleNoArgument(LauncherMode launcherMode) {
        switch (launcherMode) {
            case INTERNAL:
                handleNoArgumentInternal();
                break;
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
        if (getLoginData().isAutoLogin()) {
            //if auto login is turned on
            logger.debug("auto login is turned on, trying to sign in into the database...");
            autoLogin();
        } else {
            //if auto login is turned off
            logger.debug("auto-login is turned off, launching a basic EntryActivity...");
            Platform.runLater(() -> onActivityLaunched(showEntryActivity().getContext(), null));
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
                    .map(EntryActivity::getContext)
                    .ifPresent(Context::toFront);
        });
    }

    private void handleNoArgumentInternal() {
        Platform.runLater(() -> {
            LoginActivity.getActiveLoginActivities().stream()
                    .map(LoginActivity::getContext)
                    .findAny()
                    .ifPresentOrElse(loginActivityContext -> {
                        loginActivityContext.toFront();
                        onActivityLaunched(loginActivityContext, null);
                    }, () -> onActivityLaunched(showEntryActivity().getContext(), null));
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
                        entryActivity.getContext().showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(entryActivity.getContext(), null);
                    });
                }).auth(getLoginData().getAutoLoginDatabase(), getLoginData().getAutoLoginCredentials());

        //the login-process was successful
        if (database != null) {
            logger.debug("signed in into the auto-login database successfully, launching a MainActivity...");

            Platform.runLater(() -> {
                onActivityLaunched(showMainActivity(database).getContext(), argument);
            });
        }
    }

    private EntryActivity showEntryActivity() {
        EntryActivity entryActivity = newBasicEntryActivity();
        entryActivity.show();
        return entryActivity;
    }

    private MainActivity showMainActivity(Database database) {
        MainActivity mainActivity = new MainActivity(database, preferences, databaseTracker);
        mainActivity.show();
        return mainActivity;
    }

    private QuickLoginActivity showQuickLoginActivity(DatabaseMeta databaseMeta, DatabaseLoginListener databaseLoginListener) {
        final var quickLoginActivity = new QuickLoginActivity(databaseMeta, databaseLoginListener);
        quickLoginActivity.show();
        return quickLoginActivity;
    }

    private EntryActivity newBasicEntryActivity() {
        return new EntryActivity(this.preferences, getLoginData(), databaseTracker);
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
    protected LoginData getLoginData() {
        return preferences.get(Preferences.Key.LOGIN_DATA);
    }

    /**
     * Defines how to save the {@link LoginData} for the base {@link ActivityLauncher} object.
     */
    protected void saveLoginData(LoginData loginData) {
        preferences.editor().put(Preferences.Key.LOGIN_DATA, loginData);
    }

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
        LoginData loginData = getLoginData();
        if (!loginData.getSavedDatabases().contains(databaseMeta)) {
            logger.debug("adding the launched database into the LoginData...");
            loginData.getSavedDatabases().add(databaseMeta);
            databaseTracker.addDatabase(databaseMeta);
            saveLoginData(loginData);
        } else {
            logger.debug("The launched database is already in the login data");
        }
    }

    /**
     * Called on the UI-thread, when an "Activity" is launched
     *
     * @param context the 'activity' through the {@link Context} interface
     */
    protected void onActivityLaunched(@NotNull Context context) {
    }

    /**
     * Called on the UI-thread, when an 'activity' is launched.
     * If you want to override this in your own {@link ActivityLauncher}
     * implementation, you should call the super method, because it also
     * iterates over the possible {@link PostLaunchQueue}.
     *
     * @param context          the context
     * @param launchedDatabase the launched database; if any; may be null
     */
    protected void onActivityLaunched(@NotNull Context context, @Nullable DatabaseMeta launchedDatabase) {
        onActivityLaunched(context);
        if (postLaunchQueue != null)
            postLaunchQueue.forEach(consumer -> consumer.accept(context, launchedDatabase));
    }

    /**
     * Used for collecting actions that should be executed by the {@link ActivityLauncher}
     * when an activity is launched.
     */
    public static final class PostLaunchQueue {
        private final Queue<BiConsumer<Context, DatabaseMeta>> items = new LinkedList<>();

        public void pushItem(BiConsumer<Context, DatabaseMeta> item) {
            items.add(item);
        }

        public void removeItem(BiConsumer<Context, DatabaseMeta> item) {
            items.remove(item);
        }

        public void forEach(Consumer<BiConsumer<Context, DatabaseMeta>> action) {
            while (!items.isEmpty())
                action.accept(items.poll());
        }
    }
}
