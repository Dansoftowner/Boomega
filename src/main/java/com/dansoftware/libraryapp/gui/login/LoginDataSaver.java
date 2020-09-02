package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Deprecated
public class LoginDataSaver extends Task<Void> {

    private static final Logger logger = LoggerFactory.getLogger(LoginDataSaver.class);

    private LoginData loginData;

    public LoginDataSaver(LoginData loginData) {
        this.loginData = loginData;
        setOnFailed((WorkerStateEvent e) -> {
            logger.error("Something went wrong when trying to save loginData", e.getSource().getException());
        });
    }

    @Override
    protected Void call() throws Exception {
        logger.debug("LoginData is (before serialization): {}", loginData);
        Preferences.Editor editor = getPreferences().editor();
        editor.set(Preferences.Key.LOGIN_DATA, loginData);
        editor.commit();

        logger.debug("LoginData saved successfully");
        return null;
    }
}
