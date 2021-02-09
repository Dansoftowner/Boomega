package com.dansoftware.libraryapp.util.os

class PlatformName {
    private val platformName: String = when {
        OsInfo.isWindows() -> "Windows"
        OsInfo.isLinux() -> "Linux"
        OsInfo.isMac() -> "Mac"
        else -> System.getProperty("os.name")
    }

    override fun toString(): String = platformName
}