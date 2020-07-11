package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.db.DBMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginData {

    private List<DBMeta> lastDatabases;
    private Credentials loggedAccountCredentials;
    private int selectedAccountIndex;
    private int loggedAccountIndex;

    public LoginData() {
        this(new ArrayList<>(), -1);
    }

    public LoginData(List<DBMeta> lastDatabases, int loggedAccountIndex) {
        this.setLastDatabases(lastDatabases);
        this.loggedAccountIndex = loggedAccountIndex;
    }

    public void setLoggedDBIndex(int loggedAccountIndex) {
        this.loggedAccountIndex = loggedAccountIndex;
    }

    public List<DBMeta> getLastDatabases() {
        return lastDatabases;
    }

    public void setLastDatabases(List<DBMeta> lastDatabases) {
        this.lastDatabases = Objects.requireNonNull(lastDatabases, "lastDatabases mustn't be null");
    }

    public DBMeta getSelectedDB() {
        try {
            return this.lastDatabases.get(this.selectedAccountIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public DBMeta getLoggedDB() {
        try {
            return this.lastDatabases.get(this.loggedAccountIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getSelectedDBIndex() {
        return selectedAccountIndex;
    }

    public void setSelectedDbIndex(int selectedAccount) {
        this.selectedAccountIndex = selectedAccount;
    }

    public int getLoggedAccountIndex() {
        return loggedAccountIndex;
    }

    public Credentials getLoggedDBCredentials() {
        return loggedAccountCredentials;
    }

    public void setLoggedDBCredentials(Credentials loggedAccountCredentials) {
        this.loggedAccountCredentials = loggedAccountCredentials;
    }

    public static class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
