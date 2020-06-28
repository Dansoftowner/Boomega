package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.gui.entry.mainview.MainView;

import java.util.Optional;

public class AppEntry {

    private final LoginActivity loginActivity;

    public AppEntry() {
        this.loginActivity = new LoginActivity();
    }

    public AppEntry(AccountFactory accountFactory) {
        this.loginActivity = new LoginActivity(accountFactory.getAccount());
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginActivity.show();
        databaseOptional.ifPresent(database -> new MainView(database).show());

        return databaseOptional.isPresent();
    }
}
