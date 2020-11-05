package com.dansoftware.libraryapp.util

import oshi.PlatformEnum
import oshi.SystemInfo

/**
 * Provides utility methods for detecting the type of the particular operating system.
 *
 * @author Daniel Gyorffy
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object OsInfo {
    @JvmStatic
    fun hasTypeAndVersion(platformType: PlatformEnum, versionStarts: String): Boolean =
            hasType(platformType) && SystemInfo().operatingSystem.versionInfo.version.startsWith(versionStarts)
    @JvmStatic
    fun hasType(platformType: PlatformEnum): Boolean = (SystemInfo.getCurrentPlatformEnum() == platformType)
    @JvmStatic
    fun isWindows(): Boolean = hasType(PlatformEnum.WINDOWS)
    @JvmStatic
    fun isLinux(): Boolean = hasType(PlatformEnum.LINUX)
    @JvmStatic
    fun isMac(): Boolean = hasType(PlatformEnum.MACOSX)
    @JvmStatic
    fun isWindows10(): Boolean = hasTypeAndVersion(PlatformEnum.WINDOWS, "10")
}