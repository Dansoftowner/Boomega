package com.dansoftware.libraryapp.util

class PlatformName {
    override fun toString(): String = when {
        OsInfo.isWindows() -> "Windows"
        OsInfo.isLinux() -> "Linux"
        OsInfo.isMac() -> "Mac"
        else -> System.getProperty("os.name")
    }
}