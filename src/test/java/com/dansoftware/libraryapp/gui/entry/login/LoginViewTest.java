package com.dansoftware.libraryapp.gui.entry.login;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class LoginViewTest extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);

        LoginView loginView = new LoginView();

        primaryStage.setScene(new Scene(loginView));
        primaryStage.show();
    }
}
