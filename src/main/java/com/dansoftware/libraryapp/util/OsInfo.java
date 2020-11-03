package com.dansoftware.libraryapp.util;

import org.jetbrains.annotations.NotNull;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

/**
 * Provides utility methods for detecting the type of the particular operating system.
 *
 * @author Daniel Gyorffy
 */
public final class OsInfo {
    private OsInfo() {
    }

    public static boolean is(@NotNull PlatformEnum platformType,
                             @NotNull String versionStartsWith) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        OperatingSystem.OSVersionInfo versionInfo = operatingSystem.getVersionInfo();
        return OsInfo.is(platformType) && versionInfo.getVersion().startsWith(versionStartsWith);
    }

    public static boolean is(@NotNull PlatformEnum platformType) {
        return SystemInfo.getCurrentPlatformEnum().equals(platformType);
    }

    public static boolean isWindows() {
        return is(PlatformEnum.WINDOWS);
    }

    public static boolean isLinux() {
        return is(PlatformEnum.LINUX);
    }

    public static boolean isMac() {
        return is(PlatformEnum.MACOSX);
    }

    public static boolean isWindows10() {
        return is(PlatformEnum.WINDOWS, "10");
    }
}
