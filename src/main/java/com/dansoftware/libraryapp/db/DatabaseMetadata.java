package com.dansoftware.libraryapp.db;

import java.util.Objects;

public class DatabaseMetadata {
    private final String name;
    private final Account account;

    public DatabaseMetadata(String name, Account account) {
        this.name = Objects.isNull(name) ? account.getFile().getName() : name;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }
}
