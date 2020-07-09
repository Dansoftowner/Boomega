package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.AppConfig;
import com.dansoftware.libraryapp.appdata.config.AppConfigWriters;
import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.gui.entry.login.dbcreator.DatabaseCreatorView;
import com.dansoftware.libraryapp.main.Globals;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.dansoftware.libraryapp.main.Main.getAppConfig;

/**
 * A LoginWindow is a javaFX {@link Stage} that should be
 * used to display {@link LoginView} gui-objects.
 *
 * <p>
 * Also, when a user closes the LoginWindow, it will save the {@link LoginData} to the
 * configurations.
 */
public class LoginWindow extends Stage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginWindow.class);

    private final LoginView root;

    /**
     * Defines what happens when the user tries to close the window
     */
    private final EventHandler<WindowEvent> ON_CLOSE_REQUEST = event -> {
        Task<Void> saverTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (Objects.isNull(getAppConfig()))
                    return null;

                getAppConfig().set(AppConfig.Key.LOGIN_DATA, root.getLoginData());
                try (var writer = AppConfigWriters.newAppDataFolderWriter()) {
                    writer.write(getAppConfig());
                }

                return null;
            }
        };
        saverTask.setOnRunning(e -> root.showLoadingOverlay());
        saverTask.setOnSucceeded(e -> LOGGER.debug("LoginData saved successfully"));
        saverTask.setOnFailed(e ->
                LOGGER.error("Something went wrong when trying to save loginData", e.getSource().getException()));

        new Thread(saverTask).start();

    };

    public LoginWindow(LoginView root) {
        this.root = root;

        this.setScene(new Scene(root));
        //this.setTitle();
        this.getIcons().add(Globals.ICON);
        this.setMaximized(true);
        this.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, ON_CLOSE_REQUEST);
    }

}
