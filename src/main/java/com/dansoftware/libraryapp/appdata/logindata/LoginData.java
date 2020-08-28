package com.dansoftware.libraryapp.appdata.logindata;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginData {

    private final ObservableList<DatabaseMeta> lastDatabases;
    private DatabaseMeta selectedDatabase;
    private DatabaseMeta autoLoginDatabase;
    private Credentials autoLoginCredentials;

    public LoginData() {
        this(new ArrayList<>());
    }

    public LoginData(@NotNull List<DatabaseMeta> lastDatabases) {
        this(lastDatabases, null);
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
        this.lastDatabases = FXCollections.observableArrayList(lastDatabases);
        this.setSelectedDatabase(selectedDatabase);
        this.setAutoLoginDatabase(autoLoginDatabase);
        this.autoLoginCredentials = autoLoginCredentials;
    }

    public List<DatabaseMeta> getLastDatabases() {
        return lastDatabases;
    }

    public void setLastDatabases(List<DatabaseMeta> lastDatabases) {
        this.lastDatabases.setAll(
                Objects.requireNonNull(lastDatabases, "lastDatabases mustn't be null")
        );

        selectedDatabase = lastDatabases.contains(selectedDatabase) ? selectedDatabase : null;
        autoLoginDatabase = lastDatabases.contains(autoLoginDatabase) ? autoLoginDatabase : null;
    }

    public DatabaseMeta getSelectedDatabase() {
        return selectedDatabase;
    }

    public DatabaseMeta getAutoLoginDatabase() {
        return autoLoginDatabase;
    }

    /**
     * Sets the "selected database" but it doesn't check that the 'lastDatabases' list contains
     * it or not.
     *
     * @param selectedDatabase the database that should be selected in the login-form
     */
    void setSelectedDatabaseUnchecked(DatabaseMeta selectedDatabase) {
        this.selectedDatabase = selectedDatabase;
    }

    /**
     * Sets the "selected database".
     *
     * <p>
     * If the 'lastDatabases' list doesn't contain the database, it will automatically add
     * to that.
     *
     * @param selectedDatabase the database that should be selected in the login-form
     */
    public void setSelectedDatabase(DatabaseMeta selectedDatabase) {
        //logger.debug("Setting the selectedDatabase to {} on Thread: {}", selectedDatabase, Thread.currentThread());
        if (selectedDatabase != null && !this.lastDatabases.contains(selectedDatabase)) {
            this.lastDatabases.add(selectedDatabase);
        }

        this.selectedDatabase = selectedDatabase;
    }

    /**
     * Sets the "auto-login database" but it doesn't check that the 'lastDatabases' list contains it
     * or not.
     *
     * @param autoLoginDatabase the database that should be automatically launched when the app starts.
     */
    void setAutoLoginDatabaseUnchecked(DatabaseMeta autoLoginDatabase) {
        this.autoLoginDatabase = autoLoginDatabase;
    }

    /**
     * Sets the "auto-login database".
     *
     * <p>
     * If the 'lastDatabases' list doesn't contain the database, it will automatically add
     * it to that.
     *
     * @param autoLoginDatabase the database that should be automatically launched when the app starts.
     */
    public void setAutoLoginDatabase(DatabaseMeta autoLoginDatabase) {
        //logger.debug("Setting the autoLoginDatabase to {} on Thread: {}", selectedDatabase, Thread.currentThread());
        if (autoLoginDatabase != null && !this.lastDatabases.contains(autoLoginDatabase)) {
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

    public boolean autoLoginTurnedOn() {
        return getAutoLoginDatabase() != null;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "lastDatabases=" + lastDatabases +
                ", selectedDatabase=" + selectedDatabase +
                ", autoLoginDatabase=" + autoLoginDatabase +
                '}';
    }

    public static LoginData empty() {
        return new LoginData();
    }
}
