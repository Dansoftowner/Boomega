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

package com.dansoftware.boomega.database.sql.mysql

enum class MySQLVersion(
    val hibernateDialect: String,
    val versionNumber: String
) {

    /**
     * Represents MySQL versions prior to 5.x
     */
    _5_X_PRIOR("org.hibernate.dialect.MySQLDialect", "5.x-prior"),

    /**
     * Represents MySQL 5.x
     */
    _5_X("org.hibernate.dialect.MySQL5Dialect", "5"),

    /**
     * Represents MySQL 5.5
     */
    _5_5("org.hibernate.dialect.MySQL55Dialect", "5.5"),

    /**
     * Represents MySQL 5.7
     */
    _5_7("org.hibernate.dialect.MySQL57Dialect", "5.7"),

    /**
     * Represents MySQL 8.x+
     */
    _8("org.hibernate.dialect.MySQL8Dialect", "8");

    override fun toString() = versionNumber

    companion object {
        val default get() = _8

        fun find(versionNumber: String) =
            values().find { it.versionNumber == versionNumber } ?: _8
    }
}