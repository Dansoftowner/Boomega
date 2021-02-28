package com.dansoftware.boomega.main;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.appdata.keybindings.DefaultKeyBindings;
import com.dansoftware.boomega.appdata.logindata.LoginData;
import com.dansoftware.boomega.exception.UncaughtExceptionHandler;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.firsttime.FirstTimeActivity;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.instance.ApplicationInstanceService;
import com.dansoftware.boomega.launcher.ActivityLauncher;
import com.dansoftware.boomega.launcher.LauncherMode;
import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.dansoftware.boomega.update.UpdateSearcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Locale;

/**
 * The main class and javafx application starter.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class Main extends BaseApplication {

    private static final Logger logger;
    private static final Object initThreadLock;

    static {
        //object for synchronizing the JavaFX Launcher Thread
        initThreadLock = Main.class;

        PropertiesResponsible.setupSystemProperties();
        //we create the logger after the necessary system-properties are put
        logger = LoggerFactory.getLogger(Main.class);
        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    /**
     * The main-method of the application;
     *
     * <p>
     * Starts the {@link ApplicationInstanceService}.
     * <p>
     * If the {@link ApplicationInstanceService} didn't stop the app,
     * the main launches the application with a
     * preloader.
     *
     * @see BaseApplication#launchApp(Class, String...)
     * @see ApplicationInstanceService#open(String[])
     */
    public static void main(String[] args) {
        ApplicationInstanceService.open(args);
        BaseApplication.launchApp(Main.class, args);
    }

    @Override
    public void init() throws Exception {
        //the list that holds the actions that will be executed by the InitActivityLauncher
        final var queue = new ActivityLauncher.PostLaunchQueue();
        handleApplicationArgument(queue);
        loadPlugins(queue);
        Preferences preferences = readConfigurations(queue);

        if (!showFirstTimeActivity(preferences))
            applyBaseConfigurations(preferences);
        loadDefaultKeyBindings(preferences);

        logger.debug("Theme is: {}", Theme.getDefault());
        logger.debug("Locale is: {}", Locale.getDefault());

        final DatabaseTracker databaseTracker = DatabaseTracker.getGlobal();
        final LoginData loginData = readLoginData(preferences, databaseTracker);

        //searching for updates, if necessary
        final UpdateSearcher.UpdateSearchResult searchResult = searchForUpdates(preferences);

        //launching the main gui environment
        launchGUI(preferences, databaseTracker, loginData, searchResult, queue);
    }

    @Init
    private void launchGUI(@NotNull Preferences preferences,
                           @NotNull DatabaseTracker databaseTracker,
                           @NotNull LoginData loginData,
                           @NotNull UpdateSearcher.UpdateSearchResult searchResult,
                           @NotNull ActivityLauncher.PostLaunchQueue queue) {
        notifyPreloader("preloader.gui.build");
        new InitActivityLauncher(
                getApplicationArgs(),
                preferences,
                databaseTracker,
                loginData,
                searchResult,
                queue
        ).launch();
    }

    /**
     * Displays the application argument on the Preloader.
     * Also it pushes an action to the given queue for displaying
     * a notification message about the successful database-launch.
     *
     * @param queue the queue that will be iterated by the {@link ActivityLauncher}
     */
    @Init
    private void handleApplicationArgument(@NotNull ActivityLauncher.PostLaunchQueue queue) {
        //if a file is passed as a parameter, we show a message about it on the Preloader
        getFormattedArgument(ArgumentTransformer::transform).ifPresent(file ->
                notifyPreloader(new Preloader.FixedMessageNotification("preloader.file.open", file.getName())));
        queue.pushItem((context, launchedDatabase) -> {
            if (launchedDatabase != null) {
                context.showInformationNotification(
                        I18N.getValue("database.file.launched", launchedDatabase.getName()), null
                );
            }
        });
    }

    /**
     * Calls the {@link PluginClassLoader} and prepares a notification message about the read plugin count.
     * Also displays a preloader message about it
     *
     * @param queue the queue that will be iterated by the {@link ActivityLauncher}
     */
    @Init
    private void loadPlugins(@NotNull ActivityLauncher.PostLaunchQueue queue) {
        notifyPreloader("preloader.plugins.load");
        final PluginClassLoader pluginClassLoader = PluginClassLoader.getInstance();
        final int readPluginsCount = pluginClassLoader.getReadPluginsCount();
        if (readPluginsCount > 0)
            queue.pushItem((context, databaseMeta) -> {
                context.showInformationNotification(
                        I18N.getValue("plugins.read.count.title", readPluginsCount),
                        null,
                        Duration.minutes(1)
                );
            });
        logger.info("Plugins loaded successfully!");
    }

    /**
     * Reads the configurations into the {@link Preferences} object and notifies
     * the Preloader about it.
     * <p>
     * Also, it pushes a notification action to the given queue, that will display
     * a notification message if the reading of the configurations failed.
     *
     * @param queue the queue that will be iterated by the {@link ActivityLauncher}
     * @return the preferences object
     */
    @Init
    private Preferences readConfigurations(ActivityLauncher.PostLaunchQueue queue) {
        notifyPreloader("preloader.preferences.read");
        try {
            final Preferences preferences = Preferences.getPreferences();
            logger.info("Configurations has been read successfully!");
            return preferences;
        } catch (RuntimeException e) {
            logger.error("Couldn't read configurations ", e);
            queue.pushItem((context, databaseMeta) -> {
                context.showErrorNotification(
                        I18N.getValue("preferences.read.failed.title"), null, event -> {
                            context.showErrorDialog(
                                    I18N.getValue("preferences.read.failed.title"),
                                    I18N.getValue("preferences.read.failed.msg"), e);
                        });
            });
        }
        return Preferences.getOnlyOutputPreferences();
    }

    /**
     * Shows the {@link FirstTimeActivity} and hides/resumes the Preloader when necessary.
     *
     * @param preferences the object that holds the configurations; the first-time activity needs it
     * @return {@code true} if the first time dialog was shown; {@code false} otherwise
     * @throws InterruptedException if some threading issues became place
     */
    @Init
    private boolean showFirstTimeActivity(@NotNull Preferences preferences) throws InterruptedException {
        synchronized (initThreadLock) {
            //creating and showing a FirstTimeDialog
            if (FirstTimeActivity.isNeeded()) {
                hidePreloader();
                logger.debug("FirstTimeDialog needed");
                Platform.runLater(() -> {
                    synchronized (initThreadLock) {
                        FirstTimeActivity firstTimeDialog = new FirstTimeActivity(preferences);
                        firstTimeDialog.show();
                        initThreadLock.notify();
                    }
                });
                //we wait until the FirstTimeDialog completes
                initThreadLock.wait();
                showPreloader();
                return true;
            }
            return false;
        }
    }

    /**
     * Reads some configurations from the particular {@link Preferences} object and
     * applies them and shows preloader notifications.
     * <p>
     * Called when a {@link FirstTimeActivity} didn't launched because if it was
     * that would mean that the First time activity already applied them.
     *
     * @param preferences the preferences object
     */
    @Init
    private void applyBaseConfigurations(@NotNull Preferences preferences) {
        notifyPreloader("preloader.lang");
        Locale.setDefault(preferences.get(Preferences.Key.LOCALE));
        notifyPreloader("preloader.theme");
        Theme.setDefault(preferences.get(Preferences.Key.THEME));
    }

    /**
     * Reads the key bindings configurations
     *
     * @param preferences the preferences object
     */
    @Init
    private void loadDefaultKeyBindings(@NotNull Preferences preferences) {
        DefaultKeyBindings.loadFrom(preferences);
    }

    /**
     * Reads the {@link LoginData} from the particular {@link Preferences}
     * object and adds the saved database to the given {@link DatabaseTracker}.
     *
     * @param preferences     the preferences object
     * @param databaseTracker the database-tracker object
     * @return read {@link LoginData} object
     */
    @Init
    private LoginData readLoginData(@NotNull Preferences preferences, @NotNull DatabaseTracker databaseTracker) {
        //adding the saved databases from the login-data to DatabaseTracker
        notifyPreloader("preloader.logindata");
        LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
        loginData.getSavedDatabases().forEach(databaseTracker::addDatabase);
        return loginData;
    }

    /**
     * Searchers for updates if the configuration read from the given {@link Preferences} object
     * says that automatic update searching is turned on.
     *
     * @param preferences the preferences object
     * @return the update-search result object
     */
    @Init
    private UpdateSearcher.UpdateSearchResult searchForUpdates(@NotNull Preferences preferences) {
        if (preferences.get(Preferences.Key.SEARCH_UPDATES)) {
            notifyPreloader("preloader.update.search");
            UpdateSearcher updateSearcher = UpdateSearcher.defaultInstance();
            return updateSearcher.search();
        }
        return new UpdateSearcher.UpdateSearchResult();
    }


    @Override
    public void stop() throws Exception {
        //writing all configurations
        logger.info("Saving configurations");
        Preferences preferences = Preferences.getPreferences();
        preferences.editor().commit();

        logger.info("Shutting down application instance service");
        ApplicationInstanceService.release();

        logger.info("Closing down PluginClassLoader");
        PluginClassLoader.getInstance().close();

        //We wait 5 seconds for the background processes to terminate, then we shut down explicitly the application
        //noinspection ResultOfMethodCallIgnored
        //ExploitativeExecutor.INSTANCE.awaitTermination(1500, TimeUnit.MILLISECONDS);
        System.exit(0);
    }

    /**
     * An {@link ActivityLauncher} implementation for starting the application.
     *
     * <p>
     * When an activity is launched, it also shows an "notifier box" if
     * a new update is available.
     */
    private static final class InitActivityLauncher extends ActivityLauncher {

        private final Preferences preferences;
        private final LoginData loginData;
        private final UpdateSearcher.UpdateSearchResult searchResult;

        private InitActivityLauncher(@NotNull List<String> args,
                                     @NotNull Preferences preferences,
                                     @NotNull DatabaseTracker databaseTracker,
                                     @NotNull LoginData loginData,
                                     @NotNull UpdateSearcher.UpdateSearchResult searchResult,
                                     @NotNull PostLaunchQueue postLaunchQueue) {
            super(LauncherMode.INIT, preferences, databaseTracker, postLaunchQueue, args);
            this.preferences = preferences;
            this.loginData = loginData;
            this.searchResult = searchResult;
        }

        @Override
        protected LoginData getLoginData() {
            return loginData;
        }

        @Override
        protected void saveLoginData(LoginData loginData) {
            preferences.editor()
                    .set(Preferences.Key.LOGIN_DATA, loginData)
                    .tryCommit();
        }

        @Override
        protected void onActivityLaunched(@NotNull Context context) {
            UpdateActivity updateActivity = new UpdateActivity(context, searchResult);
            updateActivity.show(false);
        }
    }

    /**
     * We mark the methods by this annotation that are representing some subtasks
     * that is used by the {@link Application#init()} method
     */
    @Target(ElementType.METHOD)
    private @interface Init {
    }
}