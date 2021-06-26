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

package com.dansoftware.boomega.util.os

import oshi.PlatformEnum
import oshi.SystemInfo

/**
 * Provides utility methods for detecting the type of the particular operating system.
 *
 * @author Daniel Gyorffy
 */
@Suppress("unused")
object OsInfo {
    private val platformType: PlatformEnum
    private val version: String
    private val buildNumber: String
    private val name: String

    init {
        val operatingSystem = SystemInfo().operatingSystem
        val osVersionInfo = operatingSystem.versionInfo

        platformType = SystemInfo.getCurrentPlatform()
        version = osVersionInfo.version
        buildNumber = osVersionInfo.buildNumber
        name = operatingSystem.family
    }

    @JvmStatic
    fun isWindows(): Boolean = hasType(PlatformEnum.WINDOWS)

    @JvmStatic
    fun isLinux(): Boolean = hasType(PlatformEnum.LINUX)

    @JvmStatic
    fun isMac(): Boolean = hasType(PlatformEnum.MACOS)

    @JvmStatic
    fun isWindows10(): Boolean = hasTypeAndVersion(PlatformEnum.WINDOWS, "10")

    @JvmStatic
    fun getName(): String = name

    @JvmStatic
    fun getVersion(): String = version

    @JvmStatic
    fun getBuildNumber(): String = buildNumber

    @JvmStatic
    fun getPlatformVersion(): String = version

    @JvmStatic
    fun getPlatformType(): PlatformEnum = platformType

    @JvmStatic
    fun hasTypeAndVersion(platformType: PlatformEnum, versionStarts: String): Boolean =
        hasType(platformType) && version.startsWith(versionStarts)

    @JvmStatic
    fun hasType(platformType: PlatformEnum): Boolean = OsInfo.platformType == platformType
}