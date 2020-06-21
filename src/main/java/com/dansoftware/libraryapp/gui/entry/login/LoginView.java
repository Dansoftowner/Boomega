package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Optional;

import static com.dansoftware.libraryapp.locale.Bundles.getFXMLValues;

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"), getFXMLValues());
            fxmlLoader.setController(viewController);
            fxmlLoader.load();
            return viewController.getWorkbench();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
