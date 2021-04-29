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