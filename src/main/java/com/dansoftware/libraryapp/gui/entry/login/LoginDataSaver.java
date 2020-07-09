package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.AppConfig;
import com.dansoftware.libraryapp.appdata.config.AppConfigWriters;
import com.dansoftware.libraryapp.appdata.config.LoginData;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.dansoftware.libraryapp.main.Main.getAppConfig;

public class LoginDataSaver extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDataSaver.class);

    private LoginData loginData;

    public LoginDataSaver(LoginData loginData) {
        this.loginData = loginData;
        setOnFailed(e ->
                LOGGER.error("Something went wrong when trying to save loginData", e.getSource().getException()));
    }

    @Override
    protected Void call() throws Exception {
        if (Objects.isNull(getAppConfig()))
            return null;

        getAppConfig().set(AppConfig.Key.LOGIN_DATA, loginData);
        try (var writer = AppConfigWriters.newAppDataFolderWriter()) {
            writer.write(getAppConfig());
        }

        LOGGER.debug("LoginData saved successfully");
        return null;
    }
}
