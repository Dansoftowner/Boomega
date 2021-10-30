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

@file:JvmName("CommonPreferences")

package com.dansoftware.boomega.config

import com.dansoftware.boomega.config.logindata.LoginData
import com.dansoftware.boomega.config.logindata.LoginDataAdapter
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.config.ThemeAdapter
import java.time.LocalDateTime
import java.util.*

/**
 * Key for accessing the default locale.
 */
@JvmField
val LOCALE = PreferenceKey("locale", Locale::class.java, Locale::getDefault)

/**
 * Key for accessing the login data
 */
@JvmField
val LOGIN_DATA = PreferenceKey("loginData", LoginData::class.java, LoginDataAdapter(), ::LoginData)

/**
 * Key for accessing that the automatic update-searching is turned on or off
 */
@JvmField
val SEARCH_UPDATES = PreferenceKey("searchUpdates", Boolean::class.java) { true }

/**
 * Key for accessing the configured theme
 */
@JvmField
val THEME = PreferenceKey("theme", Theme::class.java, ThemeAdapter(), Theme::default)

/**
 * Key for accessing the time of the last update-search
 */
@JvmField
val LAST_UPDATE_SEARCH: PreferenceKey<LocalDateTime?> =
    PreferenceKey("searchUpdates.last", LocalDateTime::class.java) { null }