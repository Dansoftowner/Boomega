package com.dansoftware.libraryapp.db;

import org.apache.commons.lang3.StringUtils;

/**
 * A {@link Credentials} object holds the information (username + password) that
 * is necessary for creating a {@link Database} usually through a {@link com.dansoftware.libraryapp.db.processor.LoginProcessor}.
 *
 * @author Daniel Gyorffy
 * @see Database
 * @see com.dansoftware.libraryapp.db.processor.LoginProcessor
 */
public class Credentials {
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

    public boolean isAnonymous() {
        return StringUtils.isBlank(this.username) && StringUtils.isBlank(this.password);
    }

    public static Credentials anonymous() {
        return new Credentials(null, null);
    }
}
