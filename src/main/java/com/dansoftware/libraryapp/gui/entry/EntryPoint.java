package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.Login;
import com.dansoftware.libraryapp.gui.entry.mainview.MainView;
import javafx.stage.Stage;

import java.util.Optional;

public class EntryPoint {

    private final Stage primaryStage;
    private final boolean autoLogin;

    private final Login login;
    private MainView mainView;

    public EntryPoint(boolean autoLogin) {
        this(new Stage(), autoLogin);
    }

    public EntryPoint(Stage primaryStage, boolean autoLogin) {
        this.primaryStage = primaryStage;
        this.autoLogin = autoLogin;
        this.login = new Login(this);
    }

    public boolean show() {
        Optional<Database> databaseOptional = login.login();

        databaseOptional.ifPresent(database -> {
            this.mainView = new MainView(database);
            this.mainView.show();
        });

        return databaseOptional.isPresent();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public Login getLogin() {
        return login;
    }

    public MainView getMainView() {
        return mainView;
    }
}
