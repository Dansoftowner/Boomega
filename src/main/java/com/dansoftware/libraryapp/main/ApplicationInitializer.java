package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.config.*;
import com.dansoftware.libraryapp.db.DBConnection;
import com.dansoftware.libraryapp.db.DataStorage;
import com.dansoftware.libraryapp.log.GuiLog;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(ApplicationInitializer.class.getName());

    /**
     * Creates a basic ApplicationInitializer.
     */
    ApplicationInitializer() {
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
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File configurationFile = applicationDataFolder.getConfigurationFile();


        ConfigurationHolder holder = new ConfigurationHashtableHolder();
        ConfigurationReader reader = new ConfigurationXMLFileReader(configurationFile);

        try {
            //try to read the configurations
            reader.readConfigurationsTo(holder);
        } catch (IOException e) {

            try {
                //if we couldn't read the configurations we try to create a new, blank one
                FileUtils.makeOldFileOf(configurationFile);

                //notify the user about that the configurations couldn't be read and new configuration file created
                LOGGER.log(new GuiLog(Level.WARNING, e, "confighandler.newfile"));
            } catch (FileUtils.UnableToCreateFileException unableToCreateFileException) {
                /*
                  if the new file creation was unsuccessful,
                  we notify the user that the program
                  couldn't read the configurations
                */
                LOGGER.log(new GuiLog(Level.SEVERE, unableToCreateFileException, "confighandler.cantread"));
            }
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
