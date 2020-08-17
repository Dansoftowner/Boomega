package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.dansoftware.libraryapp.appdata.Preferences.getPreferences;

/**
 * A LoginDataSaver is a {@link Task} that saves the login-data
 * into the configurations file.
 *
 * <p><br>
 * Example:
 * <pre>{@code
 * Task<Void> saverTask = new LoginDataSaver(loginData);
 *
 * Thread workerThread = new Thread(saverTask);
 * workerThread.start();
 * }</pre>
 */
public class LoginDataSaver extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDataSaver.class);

    private LoginData loginData;

    public LoginDataSaver(LoginData loginData) {
        this.loginData = loginData;
        setOnFailed((WorkerStateEvent e) -> {
            LOGGER.error("Something went wrong when trying to save loginData", e.getSource().getException());
        });
    }

    @Override
    protected Void call() throws Exception {
        Preferences.Editor editor = getPreferences().editor();
        editor.set(Preferences.Key.LOGIN_DATA, loginData);
        editor.commit();

        LOGGER.debug("LoginData saved successfully");
        return null;
    }
}
