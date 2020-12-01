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
    private val platformType: PlatformEnum = SystemInfo.getCurrentPlatformEnum()
    private val version: String = SystemInfo().operatingSystem.versionInfo.version

    @JvmStatic
    fun getPlatformType(): PlatformEnum = platformType

    @JvmStatic
    fun getPlatformVersion(): String = version

    @JvmStatic
    fun hasTypeAndVersion(platformType: PlatformEnum, versionStarts: String): Boolean =
            hasType(platformType) && this.version.startsWith(versionStarts)

    @JvmStatic
    fun hasType(platformType: PlatformEnum): Boolean = this.platformType == platformType

    @JvmStatic
    fun isWindows(): Boolean = hasType(PlatformEnum.WINDOWS)

    @JvmStatic
    fun isLinux(): Boolean = hasType(PlatformEnum.LINUX)

    @JvmStatic
    fun isMac(): Boolean = hasType(PlatformEnum.MACOSX)

    @JvmStatic
    fun isWindows10(): Boolean = hasTypeAndVersion(PlatformEnum.WINDOWS, "10")
}