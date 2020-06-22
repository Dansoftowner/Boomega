package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;

import java.util.Optional;

public class LoginActivity {

    private Account initialAccount;

    public LoginActivity() {
    }

    public LoginActivity(Account initialAccount) {
        this.initialAccount = initialAccount;
    }

    public Optional<Database> show() {
        LoginView loginView = new LoginView();

        LoginWindow loginWindow = new LoginWindow(loginView);
        loginWindow.showAndWait();

        return Optional.ofNullable(loginView.getSelectedDatabase());
    }
}
