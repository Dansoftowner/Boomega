package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.locale.Bundles;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class LoginViewTest extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);

        LoginView loginView = new LoginView();
        loginView.show();

    }
}
