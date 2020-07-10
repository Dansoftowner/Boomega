package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * An Account is an object that can be used to authenticate a database.
 */
public class Account {

    private final String dbName;
    private final String username;
    private final String password;
    private final File file;

    private Account() {
        this.username = null;
        this.password = null;
        this.file = null;
        this.dbName = null;
    }

    /**
     * Creates an account with the specified filePath.
     *
     * @param file mustn't be null;
     * @throws NullPointerException if the filePath is null
     */
    public Account(File file) {
        this(file, null, null);
    }

    /**
     * Creates an account with the specified filePath.
     *
     * @param file mustn't be null;
     * @throws NullPointerException if the filePath is null
     */
    public Account(File file, String username, String password) {
        this(file, username, password, null);
    }


    public Account(File file, String username, String password, String dbName) {
        this.file = Objects.requireNonNull(file, "The filePath mustn't be null!");
        this.dbName = Objects.isNull(dbName) ? file.getName() : dbName;
        this.username = username;
        this.password = password;
    }

    public File getFile() {
        return file;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * Checks the account is anonymous or not.
     *
     * <p>
     * if both username an password are null or empty -> it is anonymous
     *
     * @return `true` if the account is anonymous; `false` otherwise.
     */
    public boolean isAnonymous() {
        return StringUtils.isBlank(this.username) && StringUtils.isBlank(this.password);
    }

    @Override
    public String toString() {
        return this.dbName + " (" + FileUtils.shortenedFilePath(file, 1) + ")";
    }

    /**
     * Creates a 100% anonymous account.
     * Neither the username, password nor the filePath is specified.
     *
     * @return the {@link Account} object
     */
    public static Account anonymous() {
        return new Account();
    }
}
