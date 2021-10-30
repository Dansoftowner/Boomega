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

package com.dansoftware.boomega.launcher

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.config.logindata.LoginData
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.main.ArgumentParser

inline fun initActivityLauncher(
    preferences: Preferences,
    databaseTracker: DatabaseTracker,
    applicationArgs: List<String>,
    crossinline getLoginData: () -> LoginData = { preferences[LOGIN_DATA] },
    crossinline setLoginData: (LoginData) -> Unit = { preferences.editor.put(LOGIN_DATA, it).tryCommit() },
    crossinline onLaunched: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit = { _, _ -> }
): ActivityLauncher = activityLauncher(
    LauncherMode.INIT,
    preferences,
    databaseTracker,
    ArgumentParser.parse(applicationArgs),
    getLoginData,
    setLoginData,
    onLaunched
)

inline fun normalActivityLauncher(
    preferences: Preferences,
    databaseTracker: DatabaseTracker,
    initialDatabase: DatabaseMeta?,
    crossinline getLoginData: () -> LoginData = { preferences[LOGIN_DATA] },
    crossinline setLoginData: (LoginData) -> Unit = { preferences.editor.put(LOGIN_DATA, it).tryCommit() },
    crossinline onLaunched: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit = { _, _ -> }
): ActivityLauncher = activityLauncher(
    LauncherMode.NORMAL,
    preferences,
    databaseTracker,
    initialDatabase,
    getLoginData,
    setLoginData,
    onLaunched
)

inline fun activityLauncher(
    mode: LauncherMode,
    preferences: Preferences,
    databaseTracker: DatabaseTracker,
    initialDatabase: DatabaseMeta?,
    crossinline getLoginData: () -> LoginData = { preferences[LOGIN_DATA] },
    crossinline setLoginData: (LoginData) -> Unit = { preferences.editor.put(LOGIN_DATA, it).tryCommit() },
    crossinline onLaunched: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit = { _, _ -> }
): ActivityLauncher = object : ActivityLauncher(
    mode = mode,
    preferences = preferences,
    databaseTracker = databaseTracker,
    initialDatabase = initialDatabase
) {
    override var loginData: LoginData
        get() = getLoginData()
        set(value) = setLoginData(value)

    override fun onActivityLaunched(context: Context, launchedDatabase: DatabaseMeta?) {
        onLaunched(context, launchedDatabase)
    }
}
