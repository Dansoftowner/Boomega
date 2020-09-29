package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.info.dependency.meta.DependencyInfo;
import com.dansoftware.libraryapp.gui.info.dependency.meta.LicenseInfo;

import java.util.List;

/**
 * Used for creating a {@link List} of {@link DependencyInfo} objects that represents the dependencies that are
 * used by this application.
 *
 * @author Daniel Gyorffy
 */
public final class DependencyLister {

    private DependencyLister() {
    }

    public static List<DependencyInfo> listDependencies() {
        return List.of(
                new DependencyInfo("JavaFX", "https://openjfx.io/", LicenseInfo.gnu2ClassPath()),
                new DependencyInfo("OpenJDK", "http://openjdk.java.net/", LicenseInfo.gnu2ClassPath()),
                new DependencyInfo("AnimateFX", "https://github.com/Typhon0/AnimateFX", LicenseInfo.apache20License()),
                new DependencyInfo("Apache Commons IO", "http://commons.apache.org/proper/commons-io/", LicenseInfo.apache20License()),
                new DependencyInfo("Apache Commons Lang", "https://commons.apache.org/proper/commons-lang/", LicenseInfo.apache20License()),
                new DependencyInfo("Gson", "https://github.com/google/gson", LicenseInfo.apache20License()),
                new DependencyInfo("Jetbrains annotations", "https://github.com/JetBrains/java-annotations", LicenseInfo.apache20License()),
                new DependencyInfo("Reflections", "https://github.com/ronmamo/reflections", LicenseInfo.wtfplLicense()),
                new DependencyInfo("SLF4J API", "http://www.slf4j.org/", LicenseInfo.mitLicense()),
                new DependencyInfo("Logback Project", "http://logback.qos.ch/", new LicenseInfo("EPL v1.0 and LGPL 2.1", "http://logback.qos.ch/license.html")),
                new DependencyInfo("jUnit 5", "https://junit.org/junit5/", new LicenseInfo("Eclipse Public License - v 2.0", "https://github.com/junit-team/junit5/blob/main/LICENSE.md")),
                new DependencyInfo("FXTaskbarProgressbar", "http://github.com/dansoftowner/fxtaskbarprogressbar", LicenseInfo.apache20License()),
                new DependencyInfo("jFileGoodies", "https://github.com/Dansoftowner/jFileGoodies", LicenseInfo.apache20License()),
                new DependencyInfo("JFoenix", "http://www.jfoenix.com/", LicenseInfo.apache20License()),
                new DependencyInfo("JMetro", "https://pixelduke.com/java-javafx-theme-jmetro/", new LicenseInfo("New BSD license", "http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29")),
                new DependencyInfo("ControlsFX", "https://github.com/controlsfx/controlsfx", new LicenseInfo("BSD 3-Clause License", "https://github.com/controlsfx/controlsfx/blob/master/license.txt")),
                new DependencyInfo("WorkbenchFX (+ with patches)", "https://github.com/dlsc-software-consulting-gmbh/WorkbenchFX", LicenseInfo.apache20License()),
                new DependencyInfo("MDFX","https://github.com/JPro-one/markdown-javafx-renderer", LicenseInfo.apache20License()),
                new DependencyInfo("Nitrite Database", "https://github.com/nitrite/nitrite-java", LicenseInfo.apache20License()),
                new DependencyInfo("JUnique", "http://www.sauronsoftware.it/projects/junique/", new LicenseInfo("LGPL", null)),
                new DependencyInfo("PDFViewerFX", "https://github.com/Dansoftowner/PDFViewerFX", LicenseInfo.apache20License())
        );
    }
}
