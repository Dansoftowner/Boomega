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

package com.dansoftware.boomega.util

import java.lang.ref.WeakReference

/**
 * An [IdentifiableWeakReference] is a [WeakReference] that provides
 * equals() and hashCode() methods that uses the referent objects equals() and hashCode() methods.
 *
 * @param <T>
 */
class IdentifiableWeakReference<T>(referent: T) : WeakReference<T>(referent) {
    override fun hashCode(): Int = get()?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is WeakReference<*> -> false
        else -> if (get() == null) super.equals(other) else get() == other.get()
    }
}