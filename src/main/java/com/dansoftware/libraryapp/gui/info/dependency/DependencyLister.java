package com.dansoftware.libraryapp.gui.info.dependency;

import java.util.List;

public final class DependencyLister {

    private DependencyLister() {
    }

    public static LicenseInfo apache20License() {
        return new LicenseInfo("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
    }

    public static List<DependencyInfo> getDependencies() {
        return List.of(
                new DependencyInfo("FXTaskbarProgressbar",
                        "http://github.com/dansoftowner/fxtaskbarprogressbar", apache20License())
        );
    }
}
