package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.entry.EntryPoint;

import java.util.Objects;
import java.util.Optional;

public class Login {

    private final EntryPoint entryPoint;

    private Account account;

    public Login(EntryPoint entryPoint) {
        this.entryPoint = Objects.requireNonNull(entryPoint, "The entryPoint mustn't be null");
    }

    public Optional<Database> login() {
        if (entryPoint.isAutoLogin()) {
            return this.quickLogin();
        }

        return this.guiLogin();
    }

    private Optional<Database> quickLogin() {
        return Optional.empty();
    }

    private Optional<Database> guiLogin() {
        return Optional.empty();
    }

    public Account getAccount() {
        return account;
    }
}
