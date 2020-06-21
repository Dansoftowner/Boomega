package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Optional;

public class LoginView {

    private final ViewController viewController;
    private Account account;

    public LoginView() {
        viewController = new ViewController();
    }

    public LoginView(Account account) {
        this();
        this.account = account;
    }

    public Optional<Database> show() {
        LoginWindow loginWindow = new LoginWindow(initGui());
        loginWindow.showAndWait();

        return Optional.ofNullable(viewController.getSelectedDatabase());
    }

    public Account getAccount() {
        return account;
    }

    private Parent initGui() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"));
            fxmlLoader.setController(viewController);
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
