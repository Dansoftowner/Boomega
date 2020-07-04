package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.appdata.config.*;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.update.loader.BaseLoader;
import com.dansoftware.libraryapp.update.notifier.GUINotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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

    private AppConfig appConfig;
    private final ApplicationArgumentHandler appArgumentHandler;

    public ApplicationInitializer(List<String> applicationParameters) {
        this.appArgumentHandler = new ApplicationArgumentHandler(applicationParameters);

        try (var reader = AppConfigReaders.newAppDataFolderReader()) {
            this.appConfig = reader.read();
            LOGGER.debug("Configurations has been read");
        } catch (IOException e) {
            this.appConfig = new AppConfig();
            LOGGER.error("Failed to load configurations ", e);
        }
    }

    /**
     *
     */
    private void setDefaults() {

        this.appArgumentHandler.getAccount().ifPresent(account -> {
            LoginData loginData = this.appConfig.get(AppConfig.Key.LOGIN_DATA);
            loginData.setLoggedAccount(account);
            this.appConfig.set(AppConfig.Key.LOGIN_DATA, loginData);
        });

        Locale.setDefault(this.appConfig.get(AppConfig.Key.LOCALE));
        LOGGER.debug("Locale set to: {}", Locale.getDefault());

        Theme.setDefault(this.appConfig.get(AppConfig.Key.THEME));
        LOGGER.debug("Theme set to: {}", Theme.getDefault());
    }

    /**
     * Checks for updates
     */
    private void searchForUpdates() {
        if (this.appConfig.get(AppConfig.Key.SEARCH_UPDATES)) {
            UpdateSearcher updateSearcher =
                    new UpdateSearcher(Globals.VERSION_INFO, new BaseLoader(), new GUINotifier());
            updateSearcher.search();
        }
    }

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void initializeApplication() {
        setDefaults();
        searchForUpdates();
    }

    public AppConfig getAppConfig() {
        return this.appConfig;
    }
}
