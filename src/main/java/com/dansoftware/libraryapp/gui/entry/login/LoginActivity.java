package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;

import java.util.Objects;
import java.util.Optional;

/**
 * A LoginActivity starts a {@link LoginWindow} with a {@link LoginView}
 * and waits for the result of the authentication.
 */
public class LoginActivity {

    private Account initialAccount;

    public LoginActivity() {
    }

    public LoginActivity(Account initialAccount) {
        this.initialAccount = initialAccount;
    }

    public Optional<Database> show() {
        LoginView loginView = new LoginView(initialAccount);

        LoginWindow loginWindow = new LoginWindow(loginView);
        loginWindow.showAndWait();

        return Optional.ofNullable(loginView.getSelectedDatabase());
    }
}
