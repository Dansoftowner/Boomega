package com.dansoftware.libraryapp.gui.info.dependency;

import java.util.List;

public final class DependencyLister {

    private DependencyLister() {
    }

    public static List<DependencyInfo> listDependencies() {
        return List.of(
                new DependencyInfo("Gson", "https://github.com/google/gson", LicenseInfo.apache20License()),
                new DependencyInfo("Jetbrains annotations", "https://github.com/JetBrains/java-annotations", LicenseInfo.apache20License()),
                new DependencyInfo("Reflections", "https://github.com/ronmamo/reflections", LicenseInfo.wtfplLicense()),
                new DependencyInfo("Apache Commons IO", "http://commons.apache.org/proper/commons-io/", LicenseInfo.apache20License()),
                new DependencyInfo("Apache Commons Lang", "https://commons.apache.org/proper/commons-lang/", LicenseInfo.apache20License()),
                new DependencyInfo("FXTaskbarProgressbar", "http://github.com/dansoftowner/fxtaskbarprogressbar", LicenseInfo.apache20License())
        );
    }
}
