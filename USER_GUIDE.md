# Boomega User Guide

#### List of contents

* [Overview](#overview)
* [Getting Boomega](#getting-boomega)
    * [Platform support](#platform-support)
    * [Running it as a JAR](#running-it-as-a-jar)
    * [Updates](#updates)
* [Using Boomega](#using-boomega)
  * [Run Boomega for the first time]()
  *
  * [Key Bindings](#key-bindings)
  * [User interface themes](#user-interface-themes)
    

## Overview

Boomega is an open-source book explorer/catalog application.
Can be used for searching books on online services (e.g. [Google Books](https://books.google.com/googlebooks/about/index.html))
and for registering/saving books to local database files.


## Getting Boomega

**You can download Boomega [here](README.md#download) or from the [releases](https://github.com/Dansoftowner/Boomega/releases) page.**

### Platform support

Boomega can run as a native application on the given platforms:

#### Windows 7, 8, 8.1, 10

Available binaries:
* **MSI Installer package (.msi)** (recommended)
* **Exe installer (.exe)**
* **Portable (.zip)**

#### Linux

Available binaries:
* **Debian Software package (.deb)** - for Debian based systems
* **Portable (.tar.xz)**

#### MacOS

MacOS-specific binaries are currently not available (help wanted), but you can still run the app [as a jar](#running-it-as-a-jar)

### Running it as a jar

You can run the cross-platform jar file with a **Java 16+** runtime that has JavaFX binaries bundled inside it (like [Zulu](https://www.azul.com/downloads/zulu-community/?package=jdk-fx) or [Liberica](https://bell-sw.com/pages/libericajdk/)).

You also have to pass the [necessary JVM options](JVM_OPTIONS.md) when you invoke `java` in the command line/terminal.  

Example:
```
java --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED   --add-exports javafx.base/com.sun.javafx.runtime=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED --add-exports javafx.controls/com.sun.javafx.scene.control.skin.resources=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED  --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.text=ALL-UNNAMED  --add-exports javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED  --add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED  --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED -jar Boomega_x.x.x-all.jar
```

### Updates

By default, Boomega is configured to check for updates automatically and notify you when a new version is available.
You will be allowed to download the binary you want immediately.

> Note: you can configure the update-searching settings at `Preferences/Settings (Ctrl+Alt+S) -> Updates`

<table>
<tr>

<td>
<img src="readme/userguide/update/Update1.png" alt="Update message">
</td>

<td>
<img src="readme/userguide/update/Update2.png" alt="Update details">
</td>

<td>
<img src="readme/userguide/update/Update3.png" alt="Downloading binaries">
</td>

</tr>
</table>

## Using Boomega

### Running Boomega for the first time

When Boomega runs for the first time, it will show a customization/configuration dialog.

#### Select the user interface theme

Select whether you want to use the **light**, **dark** or **os-synchronized** theme

![Selecting themes in the customization dialog](readme/userguide/firsttime/FirstTimeTheme.png)

For more information, see [user interface themes](#user-interface-themes).

### Key bindings

TODO

### User interface themes

TODO