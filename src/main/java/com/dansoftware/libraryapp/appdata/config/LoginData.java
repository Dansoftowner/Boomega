package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.db.Account;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.PredicateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginData {

    private List<Account> lastAccounts;
    private Account loggedAccount;

    public LoginData() {
        this(new ArrayList<>(), null);
    }

    public LoginData(List<Account> lastAccounts, Account loggedAccount) {
        this.setLastAccounts(lastAccounts);
        this.loggedAccount = loggedAccount;
    }

    public void setLoggedAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
    }

    public List<Account> getLastAccounts() {
        return lastAccounts;
    }

    @SuppressWarnings("unchecked")
    public void setLastAccounts(List<Account> lastAccounts) {
        this.lastAccounts = ListUtils.predicatedList(
                Objects.requireNonNull(lastAccounts, "lastAccounts mustn't be null"),
                PredicateUtils.notNullPredicate()
        );
    }

    public Account getLoggedAccount() {
        return loggedAccount;
    }
}
