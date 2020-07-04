package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.AppConfig;
import com.dansoftware.libraryapp.appdata.config.AppConfigWriters;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.main.Globals;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.dansoftware.libraryapp.main.Main.getAppConfig;

public class LoginWindow extends Stage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginWindow.class);

    public LoginWindow(LoginView root) {
        this.setScene(initScene(root));
        //this.setTitle();
        this.getIcons().add(Globals.WINDOW_ICON);
        this.setMaximized(true);
        this.setOnCloseRequest(event -> {
            Task<Void> saverTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
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

        });
    }

    private Scene initScene(Parent root) {
        return new Scene(root);
    }

}
