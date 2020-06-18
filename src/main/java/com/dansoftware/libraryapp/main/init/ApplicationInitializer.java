package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.appdata.config.BaseConfigurationIO;
import com.dansoftware.libraryapp.appdata.config.ConfigurationBase;
import com.dansoftware.libraryapp.appdata.config.ConfigurationIO;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.update.loader.BaseLoader;
import com.dansoftware.libraryapp.update.notifier.GUINotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * This class is used to initialize some important thing
 * before the application-gui actually starts.
 *
 * <b>Should be instantiated and used only ONCE</b>
 *
 * @see Main#init()
 */
public final class ApplicationInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationInitializer.class);

    /**
     * Reads the configurations
     */
    private void readConfigurations() {
        ConfigurationIO configurationReader = new BaseConfigurationIO();

        try {
            ConfigurationBase configurationBase = configurationReader.read();
            ConfigurationBase.setGlobal(configurationBase);
            LOGGER.debug("Configurations has been read");
        } catch (IOException e) {
            LOGGER.error("Couldn't read configurations", e);
        }


        Locale locale = ConfigurationBase.getGlobal().getLocale();
        if (locale != null) {
            Locale.setDefault(locale);
            LOGGER.debug("Locale set to: {}", Locale.getDefault());
        }

        Theme theme = ConfigurationBase.getGlobal().getTheme();
        if (theme != null) {
            Theme.setDefault(theme);
            LOGGER.debug("Theme set to: {}", theme);
        }
    }

    /**
     * Checks for updates
     */
    private void searchForUpdates() {
        if (ConfigurationBase.getGlobal().isSearchUpdatesOn()) {
            UpdateSearcher updateSearcher = new UpdateSearcher(Globals.VERSION_INFO, new BaseLoader(), new GUINotifier());
            updateSearcher.search();
        }
    }

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void initializeApplication() {
        readConfigurations();
        searchForUpdates();
    }
}
