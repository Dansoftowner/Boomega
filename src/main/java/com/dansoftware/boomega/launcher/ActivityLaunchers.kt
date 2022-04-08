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

package com.dansoftware.boomega.launcher

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.login.config.LoginData
import com.dansoftware.boomega.main.parseArguments

/**
 * Builds an [ActivityLauncher] should be used for launching the proper activity when the application starts
 */
inline fun initActivityLauncher(
    preferences: Preferences = get(Preferences::class),
    databaseTracker: DatabaseTracker = get(DatabaseTracker::class),
    applicationArgs: List<String>,
    crossinline getLoginData: () -> LoginData = { preferences[LOGIN_DATA] },
    crossinline setLoginData: (LoginData) -> Unit = { preferences.editor.put(LOGIN_DATA, it).tryCommit() },
    crossinline onLaunched: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit = { _, _ -> }
): ActivityLauncher = activityLauncher(
    LauncherMode.INITIAL,
    preferences,
    databaseTracker,
    parseArguments(applicationArgs),
    getLoginData,
    setLoginData,
    onLaunched
)

/**
 * Builds an [ActivityLauncher] should be used for "internal" operations after the app is already opened
 */
inline fun internalActivityLauncher(
    preferences: Preferences = get(Preferences::class),
    databaseTracker: DatabaseTracker = get(DatabaseTracker::class),
    initialDatabase: DatabaseMeta?,
    crossinline getLoginData: () -> LoginData = { preferences[LOGIN_DATA] },
    crossinline setLoginData: (LoginData) -> Unit = { preferences.editor.put(LOGIN_DATA, it).tryCommit() },
    crossinline onLaunched: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit = { _, _ -> }
): ActivityLauncher = activityLauncher(
    LauncherMode.INTERNAL,
    preferences,
    databaseTracker,
    initialDatabase,
    getLoginData,
    setLoginData,
    onLaunched
)

/**
 * Constructs an activity launcher from the given options
 */
inline fun activityLauncher(
    mode: LauncherMode,
    preferences: Preferences = get(Preferences::class),
    databaseTracker: DatabaseTracker = get(DatabaseTracker::class),
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