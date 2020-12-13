package com.dansoftware.libraryapp.util

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

        this.platformType = SystemInfo.getCurrentPlatformEnum()
        this.version = osVersionInfo.version
        this.buildNumber = osVersionInfo.buildNumber
        this.name = operatingSystem.family
    }

    @JvmStatic
    fun isWindows(): Boolean = hasType(PlatformEnum.WINDOWS)

    @JvmStatic
    fun isLinux(): Boolean = hasType(PlatformEnum.LINUX)

    @JvmStatic
    fun isMac(): Boolean = hasType(PlatformEnum.MACOSX)

    @JvmStatic
    fun isWindows10(): Boolean = hasTypeAndVersion(PlatformEnum.WINDOWS, "10")

    @JvmStatic
    fun isWindows10OrLater(): Boolean = hasTypeAndVersionOrHigher(PlatformEnum.WINDOWS, "10")

    @JvmStatic
    fun isMacOsMojaveOrLater(): Boolean = hasTypeAndVersionOrHigher(PlatformEnum.MACOSX, "10.14")

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
            hasType(platformType) && this.version.startsWith(versionStarts)

    @JvmStatic
    fun hasTypeAndVersionOrHigher(platformType: PlatformEnum, version: String): Boolean =
        hasType(platformType) && hasVersionOrHigher(version)

    @JvmStatic
    fun hasType(platformType: PlatformEnum): Boolean = this.platformType == platformType

    @JvmStatic
    fun hasVersionOrHigher(version: String) =
        this.version.replace(".", "").toInt() >= version.replace(".", "").toInt()
}