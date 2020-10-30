package com.dansoftware.libraryapp.util

import com.sun.javafx.PlatformUtil

class PlatformName {
    override fun toString(): String {
        return when {
            PlatformUtil.isWindows() -> "Windows"
            PlatformUtil.isLinux() -> "Linux"
            PlatformUtil.isMac() -> "Mac"
            else -> System.getProperty("os.name")
        }
    }
}