package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class LoginViewTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("hu"));

        LoginView loginView = new LoginView();
                /*new LoginView(
                        new LoginData(
                                new ArrayList<>(List.of(new Account(new File("c:/dsaf/ds.db"),
                                        null,
                                        null,
                                        "manyi"))),
                                new Account(new File("K:/fsdfdsf/f.dv"),
                                        "manxi",
                                        "null",
                                        "FBKE")));*/

        Theme.LIGHT.apply(loginView);


        primaryStage.setScene(new Scene(loginView));
        primaryStage.show();
    }
}
