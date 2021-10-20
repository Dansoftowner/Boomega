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

package com.dansoftware.boomega.config.logindata;

import com.dansoftware.boomega.database.api.DatabaseProvider;
import com.dansoftware.boomega.db.Credentials;
import com.dansoftware.boomega.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LoginData_ {

    private final ObservableList<DatabaseMeta> savedDatabases;
    private DatabaseMeta selectedDatabase;
    private Credentials autoLoginCredentials;
    private boolean autoLogin;

    public LoginData() {
        this(new ArrayList<>());
    }

    public LoginData(@NotNull List<DatabaseMeta> savedDatabases) {
        this.savedDatabases = FXCollections.observableArrayList(savedDatabases);
    }

    public void setSelectedDatabase(DatabaseMeta selectedDatabase) {
        //logger.debug("Setting the selectedDatabase to {} on Thread: {}", selectedDatabase, Thread.currentThread());
        if (selectedDatabase != null && !this.savedDatabases.contains(selectedDatabase)) {
            this.savedDatabases.add(selectedDatabase);
        }
        this.selectedDatabase = selectedDatabase;
    }

    public void setSelectedDatabaseIndex(int index) {
        try {
            this.selectedDatabase = savedDatabases.get(index);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public int getSelectedDatabaseIndex() {
        return this.savedDatabases.indexOf(selectedDatabase);
    }

    public DatabaseMeta getAutoLoginDatabase() {
        return autoLogin ? selectedDatabase : null;
    }

    public Credentials getAutoLoginCredentials() {
        return autoLoginCredentials == null ?
                Credentials.anonymous() : autoLoginCredentials;
    }

    public boolean isAutoLogin() {
        return autoLogin && selectedDatabase != null;
    }

    public void setAutoLogin(boolean value) {
        this.autoLogin = value;
    }

    public List<DatabaseMeta> getSavedDatabases() {
        return savedDatabases;
    }

    public DatabaseMeta getSelectedDatabase() {
        return selectedDatabase;
    }

    public void setAutoLoginCredentials(Credentials autoLoginCredentials) {
        this.autoLoginCredentials = autoLoginCredentials;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "savedDatabases=" + savedDatabases +
                ", selectedDatabase=" + selectedDatabase +
                ", autoLogin=" + autoLogin +
                '}';
    }

    public static LoginData empty() {
        return new LoginData();
    }
}
