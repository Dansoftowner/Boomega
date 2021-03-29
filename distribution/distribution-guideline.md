# Distributing Boomega

### Step by step
* Download a `JDK 14+` that contains the `jpackage` tool
* Create an environmental variable `JPACKAGE_HOME` that points to the previously downloaded JDK's `bin` folder
* Create an environmental variable `BOOMEGA_RUNTIME` that points to a java-runtime that can be used for running
the application on the user's system. It should have at least version `11` and it should have the `JavaFX` modules.


* Install the `Kotlin` compiler on your computer. More details [here](https://kotlinlang.org/docs/command-line.html#install-the-compiler).
* Make sure you have the `JAVA_HOME` environmental variable required by the `Kotlin` compiler.
* Make sure you have the kotlin-compiler on the system `PATH`

* Install the tools required by `jpackage`
  * **Windows**: Install the [Wix toolset](http://wixtoolset.org/)
  * **MacOS**: install the `Xcode command line tools`
  * Install the `fakeroot` package on **Ubuntu** and the `rpm-build` package on **Red Hat Linux**.

* Run the `Gradle` `jar` task
* Run the appropriate script file (`packager.bat` or `packager.sh`)

After the script is executed successfully, the binaries are placed into the `distribution/build/$version/platform` directory.

### Results:
* On **Windows**:
    * `Boomega-$version-portable_win` - the raw directory of the bundled app
    * `Boomega-$version_win-system.exe` - system-level `exe` installer
    * `Boomega-$version_win-user.exe` - user-level `exe` installer
    * `Boomega-$version_win-system.msi` - system-level `msi` installer
    * `Boomega-$version_win-user.msi` - user-level `msi` installer
  
### TODO
* Full MacOS support

> **Note**: Because the traditional kotlin script (`.kts`) does not work because of some bugs, a normal `.kt` file used instead.
> For compiling and running this, a platform-specific script is available (like `packager.bat`). It compiles down `packager.kt`
> into `Java 11` bytecode and executes it with the default java runtime.
