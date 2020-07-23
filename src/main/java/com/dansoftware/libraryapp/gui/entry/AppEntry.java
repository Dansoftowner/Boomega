package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.login.LoginActivity;
import com.dansoftware.libraryapp.gui.entry.mainview.MainView;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AppEntry {

    private final LoginActivity loginActivity;

    public AppEntry() {
        this.loginActivity = new LoginActivity();
    }

    public AppEntry(@NotNull Preferences config) {
        this.loginActivity = new LoginActivity(config.get(Preferences.Key.LOGIN_DATA));
    }

    public boolean show() {
        Optional<Database> databaseOptional = loginActivity.show();
        databaseOptional.ifPresent(database -> new MainView(database).show());

        return databaseOptional.isPresent();
    }
}
