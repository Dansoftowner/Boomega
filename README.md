<p align="center">
  <img align="center" src="/docs/img/readme/logo.png" alt="Boomega icon">
  <h1 align="center">Boomega <a href="https://github.com/Dansoftowner/Boomega"><img align="center" alt="Unstable stage" src="https://img.shields.io/badge/stage-unstable-red"></a></h1>
</p>

<p align="center">
    <a href="LICENSE"><img align="center" alt="License" src="https://img.shields.io/github/license/DansoftOwner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/issues"><img align="center" alt="Issues" src="https://img.shields.io/github/issues/DansoftOwner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/issues"><img align="center" alt="Issues closed" src="https://img.shields.io/github/issues-closed/Dansoftowner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/commits/dev"><img align="center" alt="Last commit" src="https://img.shields.io/github/last-commit/Dansoftowner/Boomega"></a>
</p>

<h3 align="center">An advanced book explorer/catalog application written in Java and Kotlin.</h3>

![Screenshot](docs/img/readme/main-activity-preview.png)

## ✨ Features

* Cross-platform
* Dark/Light theme, modern UI
* Multiple UI languages
* Flexible to support multiple Database Management Systems (e.g MySql)
* Exporting to several formats like **Excel Spreadsheets**, **JSON**, etc...
* Custom notes with **Markdown** support
* **Google Books** synchronization
* Customizable key-bindings
* Plugin support
* ...and more!

## 📄 Documentation

* [User guide](docs/USER_GUIDE.md) - Detailed documentation
* [Plugin guide](docs/PLUGIN_GUIDE.md) - Guide for writing Boomega Plugins
* [Default key-bindings](docs/DEFAULT_KEYBINDINGS.md)

## ⬇️ Download
[![Platform](https://img.shields.io/badge/platform-windows%20%7C%20macos%20%7C%20linux-lightgrey?logo=linux&logoColor=white)]()
[![Downloads](https://img.shields.io/github/downloads/DansoftOwner/Boomega/total)](https://github.com/Dansoftowner/Boomega/releases)
[![Version](https://img.shields.io/github/v/release/Dansoftowner/Boomega?include_prereleases)](https://github.com/Dansoftowner/Boomega/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date-pre/Dansoftowner/Boomega?logo=googlecalendar&logoColor=white)](https://github.com/Dansoftowner/Boomega/releases)

> Note: If you want to try the latest version, you should [build](#-build) the project yourself!

<table>

<tr>
  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/readme/windows.png" alt="">
            Windows
          </h1>
        </b>
        <p><b>(64-bit)</b></p>
  </td>

  <td align="center"> 
      <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/readme/linux.png" alt="">
            Linux
          </h1>
        </b>
      <p><b>(64-bit)</b></p>
  </td>

  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/readme/mac.png" alt="">
            MacOS 
          </h1>
        </b>
        <p><b>(64-bit)</b></p>
  </td>

</tr>

<tr>

  <td>

<b>

  <ul>
      <li>
        <h4>
           <a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/Boomega-0.7.5-win.exe">Exe installer (.exe)</a>
        </h4>
      </li>
      <li>
          <h4>
            <a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/Boomega-0.7.5-win.msi">MSI installer package (.msi)</a>
          </h4>
      </li>
      <li>
        <h4>
           <a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/Boomega-0.7.5-win.zip">Portable (.zip)</a>
        </h4>
      </li>
  </ul>

</b>
  </td>

  <td>
<b>
   <ul>
        <li>
          <h4>
            <a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/boomega_0.7.5-1_amd64-linux.deb">Debian Software Package (.deb)</a>
          </h4>
        </li>
        <li>
          <h4>
             <a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/Boomega-0.7.5-linux.tar.xz">Portable (.tar.xz)</a>
          </h4>
        </li>
   </ul>
</b>
  </td>

  <td>

  <p><b><i>Help wanted</i></b></p>

  </td>

</tr>

<tr>

  <td align="center" colspan="3">
    <b><a href="https://github.com/Dansoftowner/Boomega/releases/download/v0.7.5/Boomega-0.7.5-all.jar">Java archive (.jar)</a></b>
  </td>

</tr>
</table>

## ⌨️ Source code
[![Java version](https://img.shields.io/badge/java-17-orange?logo=java&logoColor=white)](https://jdk.java.net/17/)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.6-purple?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Top language](https://img.shields.io/github/languages/top/Dansoftowner/Boomega)](https://github.com/Dansoftowner/Boomega)
[![Gradle](https://img.shields.io/badge/gradle-7.0-green?logo=gradle&logoColor=white)](https://gradle.org/)
[![Gui](https://img.shields.io/badge/gui-javafx-blue)](https://openjfx.io/)
[![GitHub repo size](https://img.shields.io/github/repo-size/Dansoftowner/Boomega)](https://github.com/Dansoftowner/Boomega)

* The repo has two important branches:
  * `master` - usually for permanent versions
  * `dev` - for developing the app further
* The code is written in `Java` and `Kotlin` mixed.
* The GUI toolkit used is [JavaFX](https://openjfx.io/)
* The app targets `Java 17` but the project is **unmodularized**
* The build tool used is [Gradle](https://gradle.org/)
* _Want to contribute?_ See the [contribution guideline](CONTRIBUTING.md).

## 🔨 Build
The recommended IDE for building this project is [IntelliJ Idea](https://www.jetbrains.com/idea/).

### Build requirements
* JDK 17 (recommended: [OpenJDK](https://jdk.java.net/17/))

### Build manually (without using an IDE)
If you want to run the project, simply use `gradlew run`. <br/>
To build a fat jar, use `gradlew shadowJar`. <br/>
To build executable binaries, use `gradlew jpackage` ([see distribution guideline](distribution/DISTRIBUTION_GUIDELINE.md))

## 🏋️ Acknowledgements

All acknowledgements (e.g. third-party libraries) are listed in [this document](ACKNOWLEDGEMENTS.md).

## 💙 Support

If you like this project, give a ⭐!

<a href='https://ko-fi.com/K3K24JK0V' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://cdn.ko-fi.com/cdn/kofi1.png?v=3' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

## 📄 License
This software is licensed under the [GNU General Public License v3](https://en.wikipedia.org/wiki/GNU_General_Public_License).
