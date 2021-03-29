# Boomega distribution

### Requirements
* [Wix toolset](http://wixtoolset.org/) installed on Windows.
* `rpm-build` package on Red Hat Linux
* `fakeroot` package on Ubuntu
* `Xcode command line tools` on MacOS

#### Required environmental variables for running the packager script:
* `JAVA_HOME` - Java home. Should have version `11+`.
* `JPACKAGE_HOME` -  The parent directory of the `jpackage` tool. like `.../jdk/bin`
* `BOOMEGA_RUNTIME` - The java runtime we want to bundle the application with. It must have version `11+` and with `JavaFX` modules.

To run the script, the `Kotlin-jvm` compiler must be installed on the system `PATH`.

Because the traditional kotlin script (`.kts`) does not work because of some bugs, a normal `.kt` file used instead.
For compiling and running this, a platform-specific script is available (like `packager.bat`). It compiles down `packager.kt`
into `Java 11` bytecode and executes it with the default java runtime.

After the script is executed successfully, the binaries are placed into the `distribution/build/$version/platform` directory.

Binaries:
* On Windows:
    * `Boomega-$version-portable_win` - the raw directory of the bundled app
    * `Boomega-$version_win-system.exe` - system-level `exe` installer
    * `Boomega-$version_win-user.exe` - user-level `exe` installer
    * `Boomega-$version_win-system.msi` - system-level `msi` installer
    * `Boomega-$version_win-user.msi` - user-level `msi` installer
  
### TODO
* Full MacOS support