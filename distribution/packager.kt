import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

val windowsName = "win"
val linuxName = "linux"
val macName = "mac"

val platformName = calcPlatformName()

val projectDir = "${File("").absoluteFile.parentFile.absolutePath}"

val jPackage = "${System.getenv("JPACKAGE_HOME")}${File.separator}jpackage"
val runtime = "${System.getenv("BOOMEGA_RUNTIME")}"

val appVersion = "0.5.0"
val appName = "Boomega"
val description = "Boomega"
val vendor = "Dansoftware"
val destinationDir = File(File("build"), "$appVersion${File.separator}$platformName").absolutePath
val inputDir = File(projectDir, "build${File.separator}libs").absolutePath
val mainJarPath = "Boomega-$appVersion.jar"
val licenseFile = File("$projectDir/LICENSE").absolutePath
val fileAssociations = File("file-associations/file-associations-${platformName}.properties").absolutePath
val iconPath = calcIconPath()

fun main() {
    createDirs()
    Runtime.getRuntime().apply {
        getBundles().forEach {
            println("Creating bundle for '${it.name}'...")
            try {
                exec(it.commandLine().also { println("\n[DEBUG] cmd: $it\n") })
                    .also { process ->
                        println("-".repeat(100))
                        process.inputStream.printToOutput()
                        process.errorStream.printToOutput()
                        process.waitFor()
                        println("-".repeat(100).plus("\n"))
                    }
                it.renameFile(destinationDir, appVersion)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


fun getBundles(): List<Bundle> = when {
    isWindows() -> listOf(
        AppImageBundle(),
        WinMsiSystemBundle(),
        WinMsiUserBundle(),
        WinExeSystemBundle(),
        WinExeUserBundle()
    )
    isLinux() -> listOf(
        AppImageBundle(),
        LinuxDebInstallerBundle(),
        LinuxRpmInstallerBundle()
    )
    isMac() -> listOf(AppImageBundle()) //TODO: mac installers
    else -> listOf(AppImageBundle())
}

fun createDirs() {
    File(destinationDir).mkdir()
}

fun calcIconPath(): String =
    when {
        isWindows() -> "Boomega.ico"
        else -> "Boomega.png"
    }.let { File("icon/$it").path }.also {
        println("[DEBUG] Icon path: $it")
    }

fun calcPlatformName() = when {
    isWindows() -> windowsName
    isLinux() -> linuxName
    isMac() -> macName
    else -> ""
}

fun isWindows() = osNameContains(windowsName)
fun isLinux() = osNameContains(linuxName)
fun isMac() = osNameContains(macName)
fun osNameContains(value: String) = System.getProperty("os.name").toLowerCase().contains(value)

fun InputStream.printToOutput() {
    BufferedReader(InputStreamReader(this)).use {
        var line: String?
        do {
            line = it.readLine()
            line?.let { println(it) }
        } while (line != null)
    }
}

fun String?.surrounding(char: Char) = char + (this ?: "null") + char
fun String?.quotation(): String = this?.surrounding(if(isWindows()) '"' else ' ') ?: "\"\""
fun String?.cmdFormat(): String = this?.replace(Regex("(\\s|\n)+"), " ") ?: ""

interface Bundle {
    val name: String
    val type: String
    val fileToRenamePattern: Regex
    val additionalFlags: String

    fun preferredFileName(version: String): String

    fun renameFile(destinationDir: String, appVersion: String) {
        File(destinationDir).listFiles { dir, name -> fileToRenamePattern.matches(name) }
            .takeIf { it.isNotEmpty() }?.let {
                it[0].apply {
                    File(parentFile, preferredFileName(appVersion)).also {
                        println("[DEBUG] renaming '$path' to '$it'")
                        renameTo(it)
                    }
                }
            }
            ?: println("[DEBUG] the file that should be renamed is not found by the given regex: '$fileToRenamePattern'")
    }

    open fun commandLine(): String {
        return """${jPackage.quotation()}
                 --type ${type.quotation()}
                 --input ${inputDir.quotation()}
                 --app-version ${appVersion.quotation()}
                 --description ${description.quotation()} 
                 --name ${appName.quotation()}
                 --dest ${destinationDir.quotation()} 
                 --vendor ${vendor.quotation()}
                 --runtime-image ${runtime.quotation()} 
                 --icon ${iconPath.quotation()}
                 --main-jar ${mainJarPath.quotation()} 
                 ${additionalFlags}
                 --verbose
            """.cmdFormat()
    }
}

class AppImageBundle : Bundle {
    override val name: String = "app-image"
    override val type: String = "app-image"
    override val fileToRenamePattern: Regex = Regex("Boomega")
    override val additionalFlags: String = ""

    override fun preferredFileName(version: String): String {
        return "$appName-$appVersion-portable_$platformName"
    }
}

abstract class InstallerBundle : Bundle {
    override val additionalFlags: String =
        """
            --license-file ${licenseFile.surrounding('"')}
            --file-associations ${fileAssociations.surrounding('"')}
        """
}

abstract class LinuxInstallerBundle : InstallerBundle() {
    override val fileToRenamePattern: Regex
        get() = Regex("")

    override fun renameFile(destinationDir: String, appVersion: String) {
    }

    override fun preferredFileName(version: String): String = ""

    override val additionalFlags: String =
        """
           --linux-menu-group Office;
           --linux-shortcut
        """
}

class LinuxDebInstallerBundle : LinuxInstallerBundle() {
    override val name: String = "deb"
    override val type: String = "deb"
}


class LinuxRpmInstallerBundle : LinuxInstallerBundle() {
    override val name: String = "rpm"
    override val type: String = "rpm"


    override val additionalFlags: String =
        """
           ${super.additionalFlags}
           --linux-rpm-license-type "GPLv3"
        """
}

abstract class WinSystemInstallerBundle : InstallerBundle() {
    override val fileToRenamePattern: Regex
        get() = Regex("Boomega-\\d\\.\\d\\.\\d\\.$type")

    override val additionalFlags: String =
        """
            ${super.additionalFlags}
            --win-menu
            --win-dir-chooser
            --win-shortcut
        """
}

abstract class WinUserInstallerBundle : WinSystemInstallerBundle() {
    override val additionalFlags: String =
        """
            ${super.additionalFlags}
            --win-per-user-install
        """
}

class WinMsiSystemBundle : WinSystemInstallerBundle() {
    override val name: String = "msi system"
    override val type: String = "msi"

    override fun preferredFileName(version: String): String {
        return "$appName-${appVersion}_${platformName}_system.msi"
    }
}

class WinMsiUserBundle : WinUserInstallerBundle() {
    override val name: String = "msi user"
    override val type: String = "msi"

    override fun preferredFileName(version: String): String {
        return "$appName-${appVersion}_${platformName}_user.msi"
    }
}

class WinExeSystemBundle : WinSystemInstallerBundle() {
    override val name: String = "exe system"
    override val type: String = "exe"

    override fun preferredFileName(version: String): String {
        return "$appName-${appVersion}_${platformName}_system.exe"
    }
}

class WinExeUserBundle : WinUserInstallerBundle() {
    override val name: String = "exe user"
    override val type: String = "exe"

    override fun preferredFileName(version: String): String {
        return "$appName-${appVersion}_${platformName}_user.exe"
    }
}