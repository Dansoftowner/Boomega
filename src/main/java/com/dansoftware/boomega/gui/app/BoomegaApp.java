/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.app;

import com.dansoftware.boomega.config.PreferenceKey;
import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.logindata.LoginData;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.firsttime.FirstTimeActivity;
import com.dansoftware.boomega.gui.font.CustomFontsLoader;
import com.dansoftware.boomega.gui.keybinding.KeyBindings;
import com.dansoftware.boomega.gui.preloader.Preloader.MessageNotification;
import com.dansoftware.boomega.gui.preloader.Preloader.MessageNotification.Priority;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity;
import com.dansoftware.boomega.gui.window.BaseWindow;
import com.dansoftware.boomega.instance.ApplicationInstanceService;
import com.dansoftware.boomega.launcher.ActivityLauncher;
import com.dansoftware.boomega.launcher.LauncherMode;
import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.dansoftware.boomega.plugin.Plugins;
import com.dansoftware.boomega.update.Release;
import com.dansoftware.boomega.update.UpdateSearcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

/**
 * The basic implementation of the JavaFX Application.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class BoomegaApp extends BaseApplication {

    private static final Logger logger = LoggerFactory.getLogger(BoomegaApp.class);
    private static final Object initThreadLock = BoomegaApp.class;

    @Override
    public void init() throws Exception {
        //the list that holds the actions that will be executed by the InitActivityLauncher
        final var queue = new ActivityLauncher.PostLaunchQueue();
        handleApplicationArgument(queue);
        loadPlugins(queue);
        Preferences preferences = readConfigurations(queue);

        if (!showFirstTimeActivity(preferences))
            applyBaseConfigurations(preferences);
        applyAdditionalConfigurations(preferences);

        logger.debug("Theme is: {}", Theme.getDefault());
        logger.debug("Locale is: {}", Locale.getDefault());

        final DatabaseTracker databaseTracker = DatabaseTracker.getGlobal();
        final LoginData loginData = readLoginData(preferences, databaseTracker);

        //searching for updates, if necessary
        final Release searchResult = searchForUpdates(preferences);

        //launching the main gui environment
        launchGUI(preferences, databaseTracker, loginData, searchResult, queue);
    }

    @Init
    private void launchGUI(@NotNull Preferences preferences,
                           @NotNull DatabaseTracker databaseTracker,
                           @NotNull LoginData loginData,
                           @Nullable Release updateSearchResult,
                           @NotNull ActivityLauncher.PostLaunchQueue queue) {
        notifyPreloader("preloader.gui.build");
        new InitActivityLauncher(
                getApplicationArgs(),
                preferences,
                databaseTracker,
                loginData,
                updateSearchResult,
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
        getParsedArgument().ifPresent(file ->
                notifyPreloader(new MessageNotification(i18n("preloader.file.open", file.getName()), Priority.HIGH)));
        queue.pushItem((context, launchedDatabase) -> {
            if (launchedDatabase != null) {
                context.showInformationNotification(
                        i18n("database.file.launched", launchedDatabase.getName()), null
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
        Plugins.getInstance().load();
        int readPluginsCount = Plugins.getInstance().pluginFileCount();
        if (readPluginsCount > 0)
            queue.pushItem((context, databaseMeta) -> {
                context.showInformationNotification(
                        i18n("plugins.read.count.title", readPluginsCount),
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
                        i18n("preferences.read.failed.title"), null, event -> {
                            context.showErrorDialog(
                                    i18n("preferences.read.failed.title"),
                                    i18n("preferences.read.failed.msg"), e);
                        });
            });
        }
        return Preferences.empty();
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
            if (FirstTimeActivity.isNeeded(preferences)) {
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
        Locale.setDefault(preferences.get(PreferenceKey.LOCALE));
        notifyPreloader("preloader.theme");
        Theme.setDefault(preferences.get(PreferenceKey.THEME));
    }

    /**
     * Applies some configurations from the particular {@link Preferences} object.
     *
     * @param preferences the preferences object
     */
    @Init
    private void applyAdditionalConfigurations(@NotNull Preferences preferences) {
        applyWindowsOpacity(preferences);
        loadDefaultKeyBindings(preferences);
    }

    /**
     * Sets the global window opacity saved in the preferences.
     *
     * @param preferences the preferences object
     */
    private void applyWindowsOpacity(@NotNull Preferences preferences) {
        final double opacity = preferences.get(BaseWindow.GLOBAL_OPACITY_CONFIG_KEY);
        logger.debug("Global window opacity read: {}", opacity);
        BaseWindow.globalOpacity.set(opacity);
    }

    /**
     * Reads the key bindings configurations
     *
     * @param preferences the preferences object
     */
    private void loadDefaultKeyBindings(@NotNull Preferences preferences) {
        KeyBindings.loadFrom(preferences);
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
        LoginData loginData = preferences.get(PreferenceKey.LOGIN_DATA);
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
    private Release searchForUpdates(@NotNull Preferences preferences) {
        if (preferences.get(PreferenceKey.SEARCH_UPDATES)) {
            notifyPreloader("preloader.update.search");
            UpdateSearcher updateSearcher = UpdateSearcher.getDefault();
            preferences.editor().put(PreferenceKey.LAST_UPDATE_SEARCH, LocalDateTime.now());
            return updateSearcher.trySearch(e -> logger.error("Couldn't search for updates", e));
        }
        return null;
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
        private final Release updateSearchResult;

        private InitActivityLauncher(@NotNull List<String> args,
                                     @NotNull Preferences preferences,
                                     @NotNull DatabaseTracker databaseTracker,
                                     @NotNull LoginData loginData,
                                     @Nullable Release updateSearchResult,
                                     @NotNull PostLaunchQueue postLaunchQueue) {
            super(LauncherMode.INIT, preferences, databaseTracker, postLaunchQueue, args);
            this.preferences = preferences;
            this.loginData = loginData;
            this.updateSearchResult = updateSearchResult;
        }

        @Override
        protected LoginData getLoginData() {
            return loginData;
        }

        @Override
        protected void saveLoginData(LoginData loginData) {
            preferences.editor()
                    .set(PreferenceKey.LOGIN_DATA, loginData)
                    .tryCommit();
        }

        @Override
        protected void onActivityLaunched(@NotNull Context context) {
            if (updateSearchResult != null) {
                UpdateActivity updateActivity = new UpdateActivity(context, updateSearchResult);
                updateActivity.show();
            }
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
