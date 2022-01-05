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

    private val systemInfo = SystemInfo()
    private val os = systemInfo.operatingSystem
    private val osVersionInfo = os.versionInfo

    @JvmStatic val platformType: PlatformEnum by lazy(SystemInfo::getCurrentPlatform)
    @JvmStatic val version: String by lazy(osVersionInfo::getBuildNumber)
    @JvmStatic val buildNumber: String by lazy(osVersionInfo::getBuildNumber)
    @JvmStatic val name: String by lazy(os::getFamily)
    @JvmStatic val isWindows: Boolean by lazy { hasType(PlatformEnum.WINDOWS) }
    @JvmStatic val isLinux: Boolean by lazy { hasType(PlatformEnum.LINUX) }
    @JvmStatic val isMacOS: Boolean by lazy { hasType(PlatformEnum.MACOS) }
    @JvmStatic val isWindows10: Boolean by lazy { hasTypeAndVersion(PlatformEnum.WINDOWS, "10") }

    @JvmStatic
    fun hasTypeAndVersion(platformType: PlatformEnum, versionStarts: String): Boolean =
        hasType(platformType) && version.startsWith(versionStarts)

    @JvmStatic
    fun hasType(platformType: PlatformEnum): Boolean = this.platformType == platformType

    @JvmStatic
    @JvmName("isWin")
    @Deprecated(
        "Use the new isWindows property",
        ReplaceWith("isWindows", "com.dansoftware.boomega.util.os.OsInfo.isWindows")
    )
    fun isWindows(): Boolean = isWindows

    @JvmStatic
    @JvmName("isLin")
    @Deprecated(
        message = "Use the new isLinux property",
        ReplaceWith("isLinux", "com.dansoftware.boomega.util.os.OsInfo.isLinux")
    )
    fun isLinux(): Boolean = isLinux

    @JvmStatic
    @Deprecated(
        message = "Use the new isMacOS property",
        ReplaceWith("isMacOS", "com.dansoftware.boomega.util.os.OsInfo.isMacOS")
    )
    fun isMac(): Boolean = isMacOS

}