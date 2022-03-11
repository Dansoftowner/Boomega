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

@file:JvmName("I18NUtils")

package com.dansoftware.boomega.i18n

import com.dansoftware.boomega.util.Person
import org.jetbrains.annotations.Nls

fun i18n(@Nls key: String, vararg args: Any?) = I18N.getValue(key, *args)

val Person.displayName get() = I18N.getLanguagePack().displayPersonName(this)