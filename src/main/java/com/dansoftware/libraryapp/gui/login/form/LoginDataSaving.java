package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A {@link LoginDataSaving} represents a {@link Thread} that is used for
 * saving the {@link LoginData} into {@link Preferences}.
 *
 * @author Daniel Gyorffy
 */
@Deprecated
public class LoginDataSaving extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LoginDataSaving.class);

    private final Preferences preferences;
    private final LoginData loginData;

    public LoginDataSaving(@NotNull Preferences preferences, @NotNull LoginData loginData) {
        this.setName(getClass().getSimpleName());
        this.preferences = preferences;
        this.loginData = loginData;
    }

    @Override
    public void run() {
        try {

            logger.debug("LoginData: {}", loginData);
            logger.debug("LoginData credentials: {}",loginData.getAutoLoginCredentials());
            logger.debug("LoginData autoLogin: {}", loginData.isAutoLogin());
            preferences.editor()
                    .set(Preferences.Key.LOGIN_DATA, loginData)
                    .commit();
            logger.debug("Done");
        } catch (IOException e) {
            logger.error("Error saving LoginData", e);
        }
    }

}