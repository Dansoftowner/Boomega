package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import com.dansoftware.libraryapp.main.Globals;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static com.dansoftware.libraryapp.appdata.Preferences.getPreferences;

/**
 * A LoginWindow is a javaFX {@link Stage} that should be
 * used to display {@link LoginView} gui-objects.
 *
 * <p>
 * Also, when a user closes the LoginWindow, it will save the {@link LoginData} to the
 * configurations.
 */
class LoginWindow extends LibraryAppStage
        implements EventHandler<WindowEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LoginWindow.class);

    private final LoginView loginView;

    public LoginWindow(@NotNull LoginView root) {
        super("window.login.title", root);
        this.loginView = Objects.requireNonNull(root, "LoginView shouldn't be null");
        this.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this);
        this.setMaximized(true);

        Theme.applyDefault((Themeable) root);
    }

    @Override
    public void handle(WindowEvent event) {
        logger.debug("Starting a new thread for saving loginData...");
        new LoginDataSaving(loginView.getLoginData()).start();
    }

    private static class LoginDataSaving extends Thread {
        private final LoginData loginData;

        LoginDataSaving(LoginData loginData) {
            this.setName(getClass().getSimpleName());
            this.loginData = loginData;
        }

        @Override
        public void run() {
            try {
                getPreferences().editor()
                        .set(Preferences.Key.LOGIN_DATA, loginData)
                        .commit();
                logger.debug("Done");
            } catch (IOException e) {
                logger.error("Error saving LoginData", e);
            }
        }

    }
}
