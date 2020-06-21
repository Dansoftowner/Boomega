package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginView;
import com.dansoftware.libraryapp.gui.entry.mainview.MainView;

import java.util.Optional;

public class EntryPoint {

    private final LoginView loginView;

    public EntryPoint() {
        this.loginView = new LoginView();
    }

    public EntryPoint(AccountFactory accountFactory) {
        Account account = accountFactory.getAccount();
        this.loginView = new LoginView(account);
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginView.show();

        databaseOptional.ifPresent(database -> new MainView(database).show());

        return databaseOptional.isPresent();
    }
}
