package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.gui.entry.mainview.MainView;

import java.util.Optional;

public class EntryPoint {

    private final LoginActivity loginActivity;

    public EntryPoint() {
        this.loginActivity = new LoginActivity();
    }

    public EntryPoint(AccountFactory accountFactory) {
        this.loginActivity = new LoginActivity(accountFactory.getAccount());
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginActivity.show();
        databaseOptional.ifPresent(database -> new MainView(database).show());

        return databaseOptional.isPresent();
    }
}
