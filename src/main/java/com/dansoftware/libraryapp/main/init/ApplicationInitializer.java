package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    private Preferences appConfig;
    private final AppArgumentHandler appArgumentHandler;

    public ApplicationInitializer(List<String> applicationParameters) {
        this.appArgumentHandler = new AppArgumentHandler(applicationParameters);

//        try (var reader = AppConfigReaders.newAppDataFolderReader()) {
//            this.appConfig = reader.read();
//            LOGGER.debug("Configurations has been read");
//        } catch (IOException e) {
//            LOGGER.error("Couldn't close resource ", e);
//        }
    }

    /**
     *
     */
    private void setDefaults() {

//        this.appArgumentHandler.getDB().ifPresent(account -> {
//            LoginData loginData = this.appConfig.get(AppConfig.Key.LOGIN_DATA);
//            loginData.getLastDatabases().add(account);
//            loginData.setSelectedDbIndex(loginData.getLastDatabases().size() - 1);
//            this.appConfig.set(AppConfig.Key.LOGIN_DATA, loginData);
//        });
//
//        Locale.setDefault(this.appConfig.get(AppConfig.Key.LOCALE));
//        LOGGER.debug("Locale set to: {}", Locale.getDefault());
//
//        Theme.setDefault(this.appConfig.get(AppConfig.Key.THEME));
//        LOGGER.debug("Theme set to: {}", Theme.getDefault());
    }

    /**
     * Checks for updates
     */
    private void searchForUpdates() {
//        if (this.appConfig.get(AppConfig.Key.SEARCH_UPDATES)) {
//            UpdateSearcher updateSearcher =
//                    new UpdateSearcher(Globals.VERSION_INFO, new BaseLoader(), new GUINotifier());
//            updateSearcher.search();
//        }
    }

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void initializeApplication() {
        setDefaults();
        searchForUpdates();
    }

    public Preferences getAppConfig() {
        return this.appConfig;
    }
}
