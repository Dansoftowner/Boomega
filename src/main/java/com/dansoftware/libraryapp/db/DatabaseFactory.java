package com.dansoftware.libraryapp.db;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

public final class DatabaseFactory {

    private DatabaseFactory() {
    }

    public static Database getDatabase(String name, Object... args) throws UnsupportedOperationException {

        if (name.equalsIgnoreCase("nitrite")) {
            Account account = isEmpty(args) ? Account.anonymous() : (Account) args[0];
            return new NitriteDatabase(account);
        }

        return null;
    }

}
