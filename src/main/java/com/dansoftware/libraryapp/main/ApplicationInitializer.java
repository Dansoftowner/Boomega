package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.config.*;
import com.dansoftware.libraryapp.db.DataProcessor;
import com.dansoftware.libraryapp.db.DefaultDBConnection;
import com.dansoftware.libraryapp.gui.notification.GuiNotificationStrategy;
import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.update.loader.BaseLoader;
import com.dansoftware.libraryapp.update.notifier.GUINotifier;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Locale;

import static com.dansoftware.libraryapp.main.Globals.getConfigurationHolder;

/**
 * This class is used to initialize some important thing
 * before the application-gui actually starts.
 *
 * <b>Should be instantiated and used only ONCE</b>
 *
 * @see Main#init()
 */
final class ApplicationInitializer {

    /**
     * Creates a basic ApplicationInitializer.
     */
    ApplicationInitializer() {
    }

    /**
     * Sets the notification's strategy.
     */
    @Step
    private void setNotificationStrategy() {
        //we set a gui notification behaviour
        Notification.setStrategy(new GuiNotificationStrategy());
    }

    /**
     * Reads the configurations from the configurations file.
     *
     * <p>
     * If the process was unsuccessful at the first time it tries
     * to create a new Configuration file
     */
    @Step
    private void readConfigurations() {
        ConfigurationHolder holder = new ConfigurationHashtableHolder();
        ConfigurationReader reader = new ConfigurationReader(
                new DefaultConfigurationFileReadingStrategy()
        );

        try {
            reader.readConfigurationsTo(holder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Globals.setConfigurationHolder(holder);
    }

    /**
     * Calls the {@link ApplicationRunsFirstAnalyzer} to welcome the
     * new user of the application.
     */
    @Step
    private void checkAppRunsFirst() {
        ApplicationRunsFirstAnalyzer applicationRunsFirstAnalyzer
                = new ApplicationRunsFirstAnalyzer();

        applicationRunsFirstAnalyzer.analyze();
    }

    /**
     * Sets the default locale read from the user-configurations
     */
    @Step
    private void setDefaultLocale() {
        Locale configuredLocale = getConfigurationHolder()
                .getConfiguration(ConfigurationKey.DEFAULT_LOCALE);

        Locale.setDefault(configuredLocale);
    }

    /**
     * Sets the default theme of the UI.
     */
    @Step
    private void setDefaultTheme() {
        Theme.setDefault(getConfigurationHolder().getConfiguration(ConfigurationKey.DEFAULT_THEME));
    }

    /**
     * Sets the global data storage
     */
    @Step
    private void setGlobalDataStorage() {
        Globals.setDataProcessor(new DataProcessor(new DefaultDBConnection()));
    }

    /**
     * Calls the {@link UpdateSearcher} if the automatic update searching
     * turned on by the user.
     */
    @Step
    private void checkUpdates() {
        //if automatic update searching is allowed by the user
        if (getConfigurationHolder().getConfiguration(ConfigurationKey.SEARCH_FOR_UPDATES_AT_START)) {
            UpdateSearcher updateSearcher = new UpdateSearcher(Globals.VERSION_INFO, new BaseLoader(), new GUINotifier());
            updateSearcher.search();
        }
    }

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void initializeApplication() {
        setNotificationStrategy();
        readConfigurations();
        checkAppRunsFirst();
        setDefaultLocale();
        setDefaultTheme();
        setGlobalDataStorage();
        checkUpdates();
    }

    /**
     * The {@link ApplicationInitializer} uses this annotation
     * on methods that are responsible for executing a sub task in
     * the initialization process
     */
    @Target(ElementType.METHOD)
    private @interface Step {
    }
}
