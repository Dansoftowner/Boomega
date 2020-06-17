package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.gui.theme.Theme;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ConfigurationBase {

    public static final ConfigurationBase EMPTY =
            new ConfigurationBase(
                    Locale.ENGLISH,
                    Theme.LIGHT,
                    Collections.emptyList(),
                    Account.anonymous(),
                    Boolean.TRUE
            );

    private static ConfigurationBase global = EMPTY;

    private Locale locale;
    private Theme theme;
    private List<String> lastFiles;
    private Account loggedAccount;
    private boolean searchUpdates;


    public ConfigurationBase() {
    }

    private ConfigurationBase(Locale locale, Theme theme, List<String> lastFiles, Account loggedAccount, boolean searchUpdates) {
        this.locale = locale;
        this.theme = theme;
        this.lastFiles = lastFiles;
        this.loggedAccount = loggedAccount;
        this.searchUpdates = searchUpdates;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public List<String> getLastFiles() {
        return lastFiles;
    }

    public void setLastFiles(List<String> lastFiles) {
        this.lastFiles = lastFiles;
    }

    public Account getLoggedAccount() {
        return loggedAccount;
    }

    public void setLoggedAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
    }

    public boolean isSearchUpdatesOn() {
        return searchUpdates;
    }

    public void setSearchUpdates(boolean searchUpdates) {
        this.searchUpdates = searchUpdates;
    }

    public static ConfigurationBase getGlobal() {
        return global;
    }

    public static void setGlobal(ConfigurationBase global) {
        if (ConfigurationBase.global != null)
            throw new UnsupportedOperationException("The global configurationBase can't " +
                    "be instantiated more than once!");

        ConfigurationBase.global = global;
    }
}