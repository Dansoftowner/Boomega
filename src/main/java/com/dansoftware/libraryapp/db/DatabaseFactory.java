package com.dansoftware.libraryapp.db;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * DatabaseFactory is a utility class for constructing {@link Database} objects.
 */
public final class DatabaseFactory {

    private DatabaseFactory() {
    }

    /**
     * Constructs a database by it's name.
     *
     * @param name the type of the database
     * @param args some additional arguments (if the particular database needs them)
     * @return the {@link Database} object
     */
    public static Database getDatabase(String name, Object... args) {
        if ("nitrite".equalsIgnoreCase(name)) {
            Account account = isEmpty(args) ? Account.anonymous() : (Account) args[0];
            return new NitriteDatabase(account);
        }

        return null;
    }

}
