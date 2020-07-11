package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.db.Account;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LoginData {

    private List<Account> lastAccounts;
    private Credentials loggedAccountCredentials;
    private int selectedAccountIndex;
    private int loggedAccountIndex;

    public LoginData() {
        this(new ArrayList<>(), -1);
    }

    public LoginData(List<Account> lastAccounts, int loggedAccountIndex) {
        this.setLastAccounts(lastAccounts);
        this.loggedAccountIndex = loggedAccountIndex;
    }

    public void setLoggedAccountIndex(int loggedAccountIndex) {
        this.loggedAccountIndex = loggedAccountIndex;
    }

    public List<Account> getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(List<Account> lastAccounts) {
        this.lastAccounts = Objects.requireNonNull(lastAccounts, "lastAccounts mustn't be null");
    }

    public Account getSelectedAccount() {
        try {
            return this.lastAccounts.get(this.selectedAccountIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Account getLoggedAccount() {
        try {
            return this.lastAccounts.get(this.loggedAccountIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getSelectedAccountIndex() {
        return selectedAccountIndex;
    }

    public void setSelectedAccountIndex(int selectedAccount) {
        this.selectedAccountIndex = selectedAccount;
    }

    public int getLoggedAccountIndex() {
        return loggedAccountIndex;
    }

    public Credentials getLoggedAccountCredentials() {
        return loggedAccountCredentials;
    }

    public void setLoggedAccountCredentials(Credentials loggedAccountCredentials) {
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
