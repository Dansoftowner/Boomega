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

@file:JvmName("I18NUtils")

package com.dansoftware.boomega.i18n.api

import com.dansoftware.boomega.util.Person
import org.jetbrains.annotations.PropertyKey

/**
 * Alias for [I18N.getValue].
 */
fun i18n(
    @PropertyKey(resourceBundle = "com.dansoftware.boomega.i18n.Values") key: String,
    vararg args: Any?
) = I18N.getValue(key, *args)

/**
 * The full name of a person formatted by the conventions of the configured language
 */
val Person.displayName: String get() = I18N.getLanguagePack().displayPersonName(this)