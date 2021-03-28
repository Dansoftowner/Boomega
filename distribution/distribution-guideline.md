The packager script requires two environmental variables:
* `JPACKAGE_HOME` -  The parent directory of the `jpackage` tool. like `.../jdk/bin`
* `BOOMEGA_RUNTIME` - The java runtime we want to bundle the application with. It must have version `11+` and with `JavaFX` modules.

The `JPackage` tool requires the [Wix toolset](http://wixtoolset.org/) installed. There should be a `WIX` environmental 
variable that points to the installation directory.

To run the script, the `Kotlin-jvm` compiler must be installed on the system `PATH`.
The `Kotlin` compiler needs the `JAVA_HOME` variable.

After the script is executed successfully, the binaries are placed into the `distribution/build` directory.