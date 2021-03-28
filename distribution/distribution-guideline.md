The packager script requires two environmental variables:
* `JPACKAGE_HOME` -  The parent directory of the `jpackage` tool. like `.../jdk/bin`
* `BOOMEGA_RUNTIME` - The java runtime we want to bundle the application with. It must have version `11+` and with `JavaFX` modules.

To run the script, the `Kotlin-jvm` compiler must be installed on the system `PATH`.