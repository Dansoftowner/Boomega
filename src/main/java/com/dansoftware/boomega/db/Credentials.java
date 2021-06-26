/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.db;

import org.apache.commons.lang3.StringUtils;

/**
 * A {@link Credentials} object holds the information (username + password) that
 * is necessary for creating a {@link Database} usually through a {@link com.dansoftware.boomega.db.auth.DatabaseAuthenticator}.
 *
 * @author Daniel Gyorffy
 * @see Database
 * @see com.dansoftware.boomega.db.auth.DatabaseAuthenticator
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
