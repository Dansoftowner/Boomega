package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoAutoLoginTest {

    private static final Logger logger = LoggerFactory.getLogger(NoAutoLoginTest.class);

    public static void main(String[] args) {
        Preferences preferences = Preferences.getPreferences();
        LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
        if (loginData.autoLoginTurnedOn()) {
            logger.debug("Auto login turned on with: {}", loginData.getAutoLoginDatabase());
            loginData.setAutoLoginDatabase(null);
            loginData.setAutoLoginCredentials(null);
            preferences.editor().put(Preferences.Key.LOGIN_DATA, loginData);
        }

        Main.main(args);
    }
}
