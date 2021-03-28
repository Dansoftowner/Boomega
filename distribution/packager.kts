import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val windowsName = "win"
val linuxName = "nux"
val macName = "mac"

val projectDir = "${File("").absoluteFile.parentFile.absolutePath}"

val jPackage = "${System.getenv("JPACKAGE_HOME")}${File.separator}jpackage"
val runtime = "${System.getenv("BOOMEGA_RUNTIME")}"

val appVersion = "0.5.0"
val appName = "Boomega"
val description = "Boomega"
val vendor = "Dansoftware"

val destinationDir = File("build").absolutePath
val inputDir = File("../build", "libs").absolutePath
val mainJarPath = "Boomega-$appVersion.jar"
val licenseFile = File("$projectDir/LICENSE").absolutePath
val fileAssociations = File("file-associations/file-associations-${getPlatformName()}.properties").absolutePath
val iconPath = getIconPath()

createDirs()

Runtime.getRuntime().apply {
    getPackageTypes().forEach {
        println("Creating bundle for '$it'...")
        try {
            exec(buildCommandLine().also { println("\n[DEBUG] cmd: $it\n") })
                .also { process ->
                    println("-".repeat(30))
                    printToOutput(process.inputStream)
                    printToOutput(process.outputStream)
                    process.waitFor()
                    println("-".repeat(30).plus("\n"))
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getPackageTypes() = when {
    isWindows() -> arrayOf("app-image", "exe", "msi")
    isLinux() -> arrayOf("app-image", "deb")
    isMac() -> arrayOf("app-image", "dmg")
    else -> arrayOf("app-image")
}

fun buildCommandLine() =
    """${jPackage.surrounding('"')}
       -t ${it.surrounding('"')}
       --input ${inputDir.surrounding('"')}
       --app-version ${appVersion.surrounding('"')}
       --description ${description.surrounding('"')} 
       -n ${appName.surrounding('"')}
       -d ${destinationDir.surrounding('"')} 
       --vendor ${vendor.surrounding('"')}
       --runtime-image ${runtime.surrounding('"')} 
       --icon ${iconPath.surrounding('"')}
       --main-jar ${mainJarPath.surrounding('"')} 
       ${getDependentFlags(it)}
       --verbose
    """.replace(Regex("(\\s|\n)+"), " ")

fun getDependentFlags(type: String) = when {
    type == "app-image" -> ""
    arrayOf("exe", "msi").contains(type) ->
        """
        --win-menu
        --win-per-user-install
        --win-dir-chooser
        --win-shortcut
        --license-file ${licenseFile.surrounding('"')}
        --file-associations ${fileAssociations.surrounding('"')}
        """
    else ->
        """ 
        --license-file $licenseFile   
        --file-associations $fileAssociations
        """
}.replace(Regex("\\s+"), " ")

fun createDirs() {
    File(destinationDir).mkdir()
}

fun getIconPath(): String =
    when {
        isWindows() -> "Boomega.ico"
        else -> "Boomega.png"
    }.let { File("icon/$it").path }.also {
        println("[DEBUG] Icon path: $it")
    }

fun getPlatformName() = when {
    isWindows() -> windowsName
    isLinux() -> linuxName
    isMac() -> macName
    else -> ""
}

fun isWindows() = osNameContains(windowsName)
fun isLinux() = osNameContains(linuxName)
fun isMac() = osNameContains(macName)
fun osNameContains(value: String) = System.getProperty("os.name").toLowerCase().contains(value)

fun printToOutput(src: InputStream) {
    BufferedReader(InputStreamReader(src)).use {
        var line: String?
        do {
            line = it.readLine()
            line?.let { println(it) }
        } while (line != null)
    }
}

fun String?.surrounding(char: Char) = char + (this ?: "null") + char