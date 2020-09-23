package com.dansoftware.libraryapp.gui.info.dependency;

import java.util.List;

public final class DependencyLister {

    private DependencyLister() {
    }

    public static LicenseInfo apache20License() {
        return new LicenseInfo("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
    }

    public static LicenseInfo wtfplLicense() {
        return new LicenseInfo("WTFPL License", "http://www.wtfpl.net/");
    }

    public static List<DependencyInfo> getDependencies() {
        return List.of(
                new DependencyInfo("Gson", "https://github.com/google/gson", apache20License()),
                new DependencyInfo("Jetbrains annotations", "https://github.com/JetBrains/java-annotations", apache20License()),
                new DependencyInfo("Reflections", "https://github.com/ronmamo/reflections", wtfplLicense()),
                new DependencyInfo("Apache Commons IO", "http://commons.apache.org/proper/commons-io/", apache20License()),
                new DependencyInfo("Apache Commons Lang", "https://commons.apache.org/proper/commons-lang/", apache20License()),
                new DependencyInfo("FXTaskbarProgressbar",
                        "http://github.com/dansoftowner/fxtaskbarprogressbar", apache20License())
        );
    }
}
