/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.process;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker;
import com.dansoftware.boomega.gui.launch.ActivityLauncher;
import com.dansoftware.boomega.gui.launch.LauncherMode;
import com.dansoftware.boomega.gui.login.config.LoginData;
import com.dansoftware.boomega.main.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static com.dansoftware.boomega.di.DIService.get;
import static com.dansoftware.boomega.gui.login.config.GetLoginDataConfigKt.LOGIN_DATA;

/**
 * Activity launcher used for launching the right window when the user requested it by running a
 * redundant process.
 */
class ActivityLauncherImpl extends ActivityLauncher {

    private final LoginData loginData;

    public ActivityLauncherImpl(@Nullable String[] args) {
        super(LauncherMode.EXTERNAL, get(Preferences.class), get(DatabaseTracker.class), Arguments.parseArguments(List.of(args)));
        this.loginData = buildLoginData();
    }

    private LoginData buildLoginData() {
        //removing all already opened databases from the LoginData
        Set<DatabaseMeta> databaseUsing = get(DatabaseTracker.class).getUsingDatabases();
        LoginData loginData = getPreferences().get(LOGIN_DATA);
        loginData.getSavedDatabases().removeAll(databaseUsing);
        loginData.setSelectedDatabase(null);
        loginData.setAutoLoginCredentials(null);
        return loginData;
    }

    @Override
    protected @NotNull LoginData getLoginData() {
        return loginData;
    }


    @Override
    protected void onActivityLaunched(@NotNull Context context, DatabaseMeta launched) {
        //TODO: displaying message '$database launched'
    }
}