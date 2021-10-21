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

package com.dansoftware.boomega.database.api.data

/**
 * A [ServiceConnection] used for connecting/pairing a [Record]
 * to external online services like Google Books.
 *
 * @author Daniel Gyorffy
 */
class ServiceConnection {

    private val infoMap: MutableMap<String, Any?> = HashMap()

    var googleBookHandle: String?
        get() = infoMap[GOOGLE_BOOK_HANDLE]?.toString()
        set(value) {
            infoMap[GOOGLE_BOOK_HANDLE] = value
        }

    constructor()

    constructor(info: Map<String, Any?>) {
        infoMap.putAll(info)
    }

    operator fun get(key: String) = infoMap[key]

    fun getString(key: String) = this[key].toString()

    fun put(key: String, value: Any?) = infoMap.put(key, value).let { this }

    fun remove(key: String) = infoMap.remove(key)

    fun isEmpty() = infoMap.isEmpty()

    fun entries(): List<Pair<String, String>> = infoMap.map { Pair(it.key, it.value.toString()) }

    fun copy() = ServiceConnection(HashMap(this.infoMap))

    companion object {
        const val GOOGLE_BOOK_HANDLE = "google.book.handle"
    }
}