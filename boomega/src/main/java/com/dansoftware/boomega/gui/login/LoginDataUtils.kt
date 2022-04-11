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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.login.config.LoginData
import java.util.concurrent.ExecutorService

inline fun com.dansoftware.boomega.config.Preferences.updateLoginData(
    executor: ExecutorService = get(ExecutorService::class, "singleThreadExecutor"),
    crossinline action: (LoginData) -> Unit
) {
    executor.submit {
        editor.put(LOGIN_DATA, this[LOGIN_DATA].also { action(it) }).tryCommit()
    }
}

fun LoginData.removeAutoLogin() {
    isAutoLogin = false
    autoLoginCredentials = null
}

fun LoginData.isAutoLoginOn(database: DatabaseMeta): Boolean {
    return isAutoLogin && selectedDatabase == database
}