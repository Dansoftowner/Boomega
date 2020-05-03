package com.dansoftware.libraryapp.util;

import com.sun.javafx.PlatformUtil;

public class PlatformName {
    @Override
    public String toString() {
        if (PlatformUtil.isWindows()) return "Windows";
        else if (PlatformUtil.isLinux()) return "Linux";
        else if (PlatformUtil.isMac()) return "Mac";

        return System.getProperty("os.name");
    }
}
