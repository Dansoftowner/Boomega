package com.dansoftware.libraryapp.gui.info.dependency

import com.dansoftware.libraryapp.gui.info.dependency.meta.DependencyInfo
import com.dansoftware.libraryapp.gui.info.dependency.meta.LicenseInfo
import org.jetbrains.annotations.NotNull

/**
 * Used for creating a [List] of [DependencyInfo] objects that represents the dependencies that are
 * used by this application.
 *
 * @author Daniel Gyorffy
 */
object DependencyLister {
    @JvmStatic
    fun listDependencies(): @NotNull List<DependencyInfo> = listOf(
            DependencyInfo("JavaFX", "https://openjfx.io/", LicenseInfo.gnu2ClassPath()),
            DependencyInfo("OpenJDK", "http://openjdk.java.net/", LicenseInfo.gnu2ClassPath()),
            DependencyInfo("OSHI", "https://github.com/oshi/oshi", LicenseInfo.mitLicense()),
            DependencyInfo("AnimateFX", "https://github.com/Typhon0/AnimateFX", LicenseInfo.apache20License()),
            DependencyInfo("FontAwesomeFX", "https://bitbucket.org/Jerady/fontawesomefx/src/master/", LicenseInfo.apache20License()),
            DependencyInfo("Apache Commons IO", "http://commons.apache.org/proper/commons-io/", LicenseInfo.apache20License()),
            DependencyInfo("Apache Commons Lang", "https://commons.apache.org/proper/commons-lang/", LicenseInfo.apache20License()),
            DependencyInfo("Gson", "https://github.com/google/gson", LicenseInfo.apache20License()),
            DependencyInfo("Jetbrains annotations", "https://github.com/JetBrains/java-annotations", LicenseInfo.apache20License()),
            DependencyInfo("JRegistry", "https://jregistry.sourceforge.io/", LicenseInfo.lgpLv2()),
            DependencyInfo("Jasypt", "http://www.jasypt.org/index.html", LicenseInfo.apache20License()),
            DependencyInfo("jUnit 5", "https://junit.org/junit5/", LicenseInfo("Eclipse Public License - v 2.0", "https://github.com/junit-team/junit5/blob/main/LICENSE.md")),
            DependencyInfo("jFileGoodies", "https://github.com/Dansoftowner/jFileGoodies", LicenseInfo.apache20License()),
            DependencyInfo("JFoenix", "http://www.jfoenix.com/", LicenseInfo.apache20License()),
            DependencyInfo("JMetro", "https://pixelduke.com/java-javafx-theme-jmetro/", LicenseInfo("New BSD license", "http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29")),
            DependencyInfo("JUnique", "http://www.sauronsoftware.it/projects/junique/", LicenseInfo("LGPL", null)),
            DependencyInfo("Reflections", "https://github.com/ronmamo/reflections", LicenseInfo.wtfplLicense()),
            DependencyInfo("SLF4J API", "http://www.slf4j.org/", LicenseInfo.mitLicense()),
            DependencyInfo("Logback Project", "http://logback.qos.ch/", LicenseInfo("EPL v1.0 and LGPL 2.1", "http://logback.qos.ch/license.html")),
            DependencyInfo("FXTaskbarProgressbar", "http://github.com/dansoftowner/fxtaskbarprogressbar", LicenseInfo.apache20License()),
            DependencyInfo("ControlsFX", "https://github.com/controlsfx/controlsfx", LicenseInfo("BSD 3-Clause License", "https://github.com/controlsfx/controlsfx/blob/master/license.txt")),
            DependencyInfo("WorkbenchFX (+ with patches)", "https://github.com/dlsc-software-consulting-gmbh/WorkbenchFX", LicenseInfo.apache20License()),
            DependencyInfo("MDFX", "https://github.com/JPro-one/markdown-javafx-renderer", LicenseInfo.apache20License()),
            DependencyInfo("Nitrite Database", "https://github.com/nitrite/nitrite-java", LicenseInfo.apache20License()),
            DependencyInfo("PDFViewerFX", "https://github.com/Dansoftowner/PDFViewerFX", LicenseInfo.apache20License())
    )
}