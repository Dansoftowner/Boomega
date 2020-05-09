package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.config.*;
import com.dansoftware.libraryapp.db.DBConnection;
import com.dansoftware.libraryapp.db.DataStorage;
import com.dansoftware.libraryapp.gui.notification.GuiNotificationStrategy;
import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.update.UpdateSearcher;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Locale;

import static com.dansoftware.libraryapp.main.Globals.getConfigurationHolder;

/**
 * This class is used to initialize some important thing
 * before the application actually starts.
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
        ConfigurationReader reader = new ConfigurationReader(new DefaultConfigurationFileReadingStrategy());

        try {
            reader.readConfigurationsTo(holder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Globals.setConfigurationHolder(holder);
    }

    @Step
    private void checkAppRunsFirst() {
        ApplicationRunsFirstAnalyzer applicationRunsFirstAnalyzer
                = new ApplicationRunsFirstAnalyzer();

        applicationRunsFirstAnalyzer.analyze();
    }

    @Step
    private void setDefaultLocale() {
        Locale configuredLocale = getConfigurationHolder()
                .getConfiguration(ConfigurationKey.DEFAULT_LOCALE);

        Locale.setDefault(configuredLocale);
    }

    @Step
    private void setGlobalDataStorage() {
        Globals.setDataStorage(new DataStorage(DBConnection.getInstance()));
    }

    @Step
    private void checkUpdates() {
        //if automatic update searching is allowed by the user
        if (getConfigurationHolder().getConfiguration(ConfigurationKey.SEARCH_FOR_UPDATES_AT_START)) {
            UpdateSearcher updateSearcher = new UpdateSearcher();
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
