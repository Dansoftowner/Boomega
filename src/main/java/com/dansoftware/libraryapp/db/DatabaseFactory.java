package com.dansoftware.libraryapp.db;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * DatabaseFactory is a utility class for constructing {@link Database} objects.
 */
public final class DatabaseFactory {

    public static final String NITRITE = "nitrite";

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
        if (NITRITE.equals(name)) {
            Account account = isEmpty(args) ? Account.anonymous() : (Account) args[0];
            String dbName = args.length > 1 ? args[1].toString() : null;
            return new NitriteDatabase(dbName, account);
        }

        return null;
    }

}
