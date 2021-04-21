package com.dansoftware.boomega.gui.info.dependency

import com.dansoftware.boomega.gui.info.dependency.meta.DependencyInfo
import com.dansoftware.boomega.gui.info.dependency.meta.LicenseInfo

/**
 * Used for creating a [List] of [DependencyInfo] objects that represents the dependencies that are
 * used by this application.
 *
 * @author Daniel Gyorffy
 */
object DependencyLister {
    @JvmStatic
    fun listDependencies(): List<DependencyInfo> = listOf(
        DependencyInfo(
            "Apache Commons IO",
            "http://commons.apache.org/proper/commons-io/",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Apache Commons Lang",
            "https://commons.apache.org/proper/commons-lang/",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "AnimateFX",
            "https://github.com/Typhon0/AnimateFX",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Caffeine",
            "https://github.com/ben-manes/caffeine",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "ControlsFX",
            "https://github.com/controlsfx/controlsfx",
            LicenseInfo("BSD 3-Clause License", "https://github.com/controlsfx/controlsfx/blob/master/license.txt")
        ),
        DependencyInfo(
            "FontAwesomeFX",
            "https://bitbucket.org/Jerady/fontawesomefx/src/master/",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "FXTaskbarProgressbar",
            "http://github.com/dansoftowner/fxtaskbarprogressbar",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "GestureFX",
            "https://github.com/tom91136/GestureFX",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Gson",
            "https://github.com/google/gson",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "JavaFX",
            "https://openjfx.io/",
            LicenseInfo.gnu2ClassPath()
        ),
        DependencyInfo(
            "Jasypt",
            "http://www.jasypt.org/index.html",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Jetbrains annotations",
            "https://github.com/JetBrains/java-annotations",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "JNA",
            "https://github.com/java-native-access/jna",
            LicenseInfo("Apache-2.0 OR LGPL-2.1", "https://github.com/java-native-access/jna/blob/master/LICENSE")
        ),
        DependencyInfo(
            "jSystemThemeDetector",
            "https://github.com/Dansoftowner/jSystemThemeDetector",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "jHTML2Md",
            "https://github.com/nico2sh/jHTML2Md",
            LicenseInfo("No license", "about:blank")
        ),
        DependencyInfo(
            "jsoup",
            "https://jsoup.org/",
            LicenseInfo.mitLicense()
        ),
        DependencyInfo(
            "jUnit 5",
            "https://junit.org/junit5/",
            LicenseInfo(
                "Eclipse Public License - v 2.0",
                "https://github.com/junit-team/junit5/blob/main/LICENSE.md"
            )
        ),
        DependencyInfo(
            "jFileGoodies",
            "https://github.com/Dansoftowner/jFileGoodies",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "JFoenix",
            "http://www.jfoenix.com/",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "JMetro",
            "https://pixelduke.com/java-javafx-theme-jmetro/",
            LicenseInfo(
                "New BSD license",
                "http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29"
            )
        ),
        DependencyInfo(
            "JUnique",
            "http://www.sauronsoftware.it/projects/junique/",
            LicenseInfo("LGPL", null)
        ),
        DependencyInfo(
            "Kotlin",
            "https://kotlinlang.org",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Logback Project",
            "http://logback.qos.ch/",
            LicenseInfo("EPL v1.0 and LGPL 2.1", "http://logback.qos.ch/license.html")
        ),
        DependencyInfo(
            "MarkdownEditorControlFX",
            "https://github.com/Dansoftowner/MarkdownEditorControlFX",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "MDFX",
            "https://github.com/JPro-one/markdown-javafx-renderer",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "Nitrite Database",
            "https://github.com/nitrite/nitrite-java",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "NSMenuFX",
            "https://github.com/0x4a616e/NSMenuFX",
            LicenseInfo("BSD-3-Clause License", "https://opensource.org/licenses/BSD-3-Clause")
        ),
        DependencyInfo(
            "OpenJDK",
            "http://openjdk.java.net/",
            LicenseInfo.gnu2ClassPath()
        ),
        DependencyInfo(
            "OSHI",
            "https://github.com/oshi/oshi",
            LicenseInfo.mitLicense()
        ),
        DependencyInfo(
            "OkHttp",
            "https://square.github.io/okhttp/",
            LicenseInfo.apache20License()
        ),
        DependencyInfo(
            "RichTextFX",
            "https://github.com/FXMisc/RichTextFX",
            LicenseInfo("BSD-2-Clause License", "https://github.com/FXMisc/RichTextFX/blob/master/LICENSE")
        ),
        DependencyInfo(
            "SLF4J API",
            "http://www.slf4j.org/",
            LicenseInfo.mitLicense()
        ),
        DependencyInfo(
            "WorkbenchFX (+ with patches)",
            "https://github.com/dlsc-software-consulting-gmbh/WorkbenchFX",
            LicenseInfo.apache20License()
        )
    )
}