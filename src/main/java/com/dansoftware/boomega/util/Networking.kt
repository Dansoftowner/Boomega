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

@file:JvmName("NetworkingUtils")

package com.dansoftware.boomega.util

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 * Regular expression representing a host address
 */
val HOST_PATTERN by lazy {
    Regex(
        "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|localhost|((www\\.)?[a-zA-Z0-9]+\\.[a-zA-Z]+)"
    )
}

/**
 * Regular expression representing a port number.
 *
 * > NOTE: it can't check whether the port number is in range of 0 and 65 535.
 * > You can use the [isValidPortNumber] for full verification.
 */
val PORT_PATTERN by lazy { Regex("\\d{1,5}") }

/**
 * Checks whether the given host address is in valid format or not
 */
fun isValidHostAddress(host: String) = HOST_PATTERN.matches(host)

/**
 * Checks whether the port number (given as string) is in valid format or not
 */
fun isValidPortNumber(portString: String) =
    PORT_PATTERN.matches(portString) && isValidPortNumber(portString.toInt())

/**
 * Checks whether the given port number is in range of `1` and `65 535`
 */
fun isValidPortNumber(port: Int) = port in 1..65_535

/**
 * Checks whether the server (that the url points to) is reachable or not
 */
fun isServerReachable(url: String) =
    try {
        URL(url).openConnection().connect()
        true
    } catch (e: MalformedURLException) {
        false
    } catch (e: IOException) {
        false
    }