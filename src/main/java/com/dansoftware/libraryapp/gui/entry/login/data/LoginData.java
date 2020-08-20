package com.dansoftware.libraryapp.gui.entry.login.data;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginData {

    private List<DatabaseMeta> lastDatabases;
    private DatabaseMeta selectedDatabase;
    private DatabaseMeta autoLoginDatabase;
    private Credentials autoLoginCredentials;
    //private int selectedAccountIndex;
    //private int loggedAccountIndex;


    public LoginData() {
        this(new ArrayList<>(), null);
    }

    public LoginData(@NotNull List<DatabaseMeta> lastDatabases,
                     @Nullable DatabaseMeta selectedDatabase) {
        this(lastDatabases, selectedDatabase, null);
    }

    public LoginData(@NotNull List<DatabaseMeta> lastDatabases,
                     @Nullable DatabaseMeta selectedDatabase,
                     @Nullable DatabaseMeta autoLoginDatabase) {
        this(lastDatabases, selectedDatabase, autoLoginDatabase, null);
    }

    public LoginData(@NotNull List<DatabaseMeta> lastDatabases,
                     @Nullable DatabaseMeta selectedDatabase,
                     @Nullable DatabaseMeta autoLoginDatabase,
                     @Nullable Credentials autoLoginCredentials) {
        this.lastDatabases = lastDatabases;
        this.selectedDatabase = selectedDatabase;
        this.autoLoginDatabase = autoLoginDatabase;
        this.autoLoginCredentials = autoLoginCredentials;
    }

    public List<DatabaseMeta> getLastDatabases() {
        return lastDatabases;
    }

    public void setLastDatabases(List<DatabaseMeta> lastDatabases) {
        this.lastDatabases = Objects.requireNonNull(lastDatabases, "lastDatabases mustn't be null");
    }

    public DatabaseMeta getSelectedDatabase() {
        return selectedDatabase;
    }

    public void setSelectedDatabase(DatabaseMeta selectedDatabase) {
        if (!this.lastDatabases.contains(selectedDatabase)) {
            this.lastDatabases.add(selectedDatabase);
        }
        this.selectedDatabase = selectedDatabase;
    }

    public DatabaseMeta getAutoLoginDatabase() {
        return autoLoginDatabase;
    }

    public void setAutoLoginDatabase(DatabaseMeta autoLoginDatabase) {
        if (this.lastDatabases.contains(autoLoginDatabase)) {
            this.lastDatabases.add(autoLoginDatabase);
        }
        this.autoLoginDatabase = autoLoginDatabase;
    }

    public Credentials getAutoLoginCredentials() {
        return autoLoginCredentials == null ?
                Credentials.anonymous() : autoLoginCredentials;
    }

    public void setAutoLoginCredentials(Credentials autoLoginCredentials) {
        this.autoLoginCredentials = autoLoginCredentials;
    }

    public static LoginData empty() {
        return new LoginData();
    }

}
