package com.dansoftware.libraryapp.db;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * An Account is an object that can be used to authenticate a database.
 */
public class Account {

    private final String username;
    private final String password;
    private final String filePath;

    private Account() {
        this.username = null;
        this.password = null;
        this.filePath = null;
    }

    /**
     * Creates an account with the specified filePath.
     *
     * @param filePath mustn't be null;
     * @throws NullPointerException if the filePath is null
     */
    public Account(String filePath) {
        this(filePath, null, null);
    }

    /**
     * Creates an account with the specified filePath.
     *
     * @param filePath mustn't be null;
     * @throws NullPointerException if the filePath is null
     */
    public Account(String filePath, String username, String password) {
        this.filePath = Objects.requireNonNull(filePath, "The filePath mustn't be null!");
        this.username = username;
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
        return StringUtils.isEmpty(this.username) && StringUtils.isEmpty(this.password);
    }

    @Override
    public String toString() {
        return filePath;
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
