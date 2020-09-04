package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.main.Globals;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private LoginView root;

    public LoginWindow(LoginView root) {
        this.root = root;

        this.setScene(new Scene(root));
        //this.setTitle();
        this.getIcons().addAll(Globals.windowIconPack());
        this.setMaximized(true);
        Theme.applyDefault((Themeable) root);
    }

}
