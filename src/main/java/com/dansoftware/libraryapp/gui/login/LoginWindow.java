package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.login.form.LoginDataSaving;
import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

    private final Preferences preferences;
    private final LoginView loginView;

    public LoginWindow(@NotNull LoginView root, @NotNull Preferences preferences) {
        super("window.login.title", " - ", root.titleProperty(), root);
        this.preferences = Objects.requireNonNull(preferences);
        this.loginView = Objects.requireNonNull(root, "LoginView shouldn't be null");
        this.setFullScreenExitHint(I18N.getGeneralWord("window.fullscreen.hint"));
        this.setFullScreenKeyCombination(new KeyCodeCombination(KeyCode.F11));
        this.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this);
        this.setMaximized(true);
    }

    @Override
    public void handle(WindowEvent event) {
        logger.debug("Starting a new thread for saving loginData...");
        new LoginDataSaving(preferences, loginView.getLoginData()).start();
    }
}
