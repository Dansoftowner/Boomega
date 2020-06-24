package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;

import java.util.Optional;

/**
 * A LoginActivity can be used for starting easily a {@link LoginWindow} with a {@link LoginView}.
 *
 * <p>
 * It can be started by the {@link LoginActivity#show()}.
 */
public class LoginActivity {

    private Account initialAccount;

    public LoginActivity() {
    }

    public LoginActivity(Account initialAccount) {
        this.initialAccount = initialAccount;
    }

    /**
     * Waits until the user signs in or closes the login window, then
     * returns the selected {@link Database} wrapped in an {@link Optional}.
     * If the {@link Optional} is empty that means that the user closed the
     * {@link LoginWindow}.
     *
     * @return the selected {@link Database} wrapped in an {@link Optional}.
     */
    public Optional<Database> show() {
        LoginView loginView = new LoginView(initialAccount);

        LoginWindow loginWindow = new LoginWindow(loginView);
        loginWindow.showAndWait();

        return Optional.ofNullable(loginView.getSelectedDatabase());
    }
}
