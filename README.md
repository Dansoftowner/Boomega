<p align="center">
  <img align="center" src="img/logo.png" alt="Boomega icon">
  <h1 align="center">Boomega</h1>
</p>

<p align="center">
    <a href="LICENSE"><img align="center" alt="License" src="https://img.shields.io/github/license/DansoftOwner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/issues"><img align="center" alt="Issues" src="https://img.shields.io/github/issues/DansoftOwner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/issues"><img align="center" alt="Issues closed" src="https://img.shields.io/github/issues-closed/Dansoftowner/Boomega"></a>
    <a href="https://github.com/Dansoftowner/Boomega/commits/dev"><img align="center" alt="Last commit" src="https://img.shields.io/github/last-commit/Dansoftowner/Boomega"></a>
</p>

<h3 align="center">An advanced book explorer/catalog application written in Java and Kotlin.</h3>

![Screenshot](img/main-activity-preview.png)

## :sparkles: Features

* Cross-platform
* Dark/Light theme, modern UI
* Multiple UI languages
* Exporting to several formats like **Excel Spreadsheets**, **JSON**, etc...
* Custom notes with **Markdown** support
* **Google Books** synchronization
* Customizable key-bindings
* Plugin support
* ...and more!

## :page_facing_up: Documentation

The detailed user guide is available [here](USER_GUIDE.md).

## :arrow_down: Download
[![Platform](https://img.shields.io/badge/platform-windows%20%7C%20macos%20%7C%20linux-lightgrey?logo=linux&logoColor=white)]()
[![Downloads](https://img.shields.io/github/downloads/DansoftOwner/Boomega/total)](https://github.com/Dansoftowner/Boomega/releases)
[![Version](https://img.shields.io/github/v/release/Dansoftowner/Boomega?include_prereleases)](https://github.com/Dansoftowner/Boomega/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date-pre/Dansoftowner/Boomega?logo=googlecalendar&logoColor=white)](https://github.com/Dansoftowner/Boomega/releases)

<table>

<tr>
  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="img/windows.png" alt="">
            Windows
          </h1>
        </b>
        <p><b>(64-bit)</b></p>
  </td>

  <td align="center"> 
      <b>
          <h1>
            <img style="margin-right: 10px" src="img/linux.png" alt="">
            Linux
          </h1>
        </b>
      <p><b>(64-bit)</b></p>
  </td>

  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="img/mac.png" alt="">
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

## Source code
[![Java version](https://img.shields.io/badge/java-16-orange?logo=java&logoColor=white)](https://jdk.java.net/16/)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.5-purple?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/gradle-7.0-green?logo=gradle&logoColor=white)](https://gradle.org/)
[![Gui](https://img.shields.io/badge/gui-javafx-blue)](https://openjfx.io/)
[![GitHub repo size](https://img.shields.io/github/repo-size/Dansoftowner/Boomega)](https://github.com/Dansoftowner/Boomega)

* The repo has two important branches:
  * `master` - for stable versions
  * `dev` - for developing the app further
* The code is written in `Java` and `Kotlin` mixed.
* The GUI toolkit used is [JavaFX](https://openjfx.io/)
* The app targets `Java 16+` but the project is `unmodularized`
* The build tool used is [Gradle](https://gradle.org/)
* _Want to contribute?_ See the [contribution guideline](CONTRIBUTING.md).

## :hammer: Build
The recommended IDE for building this project is `IntelliJ Idea`.

### Build requirements
* JDK 16+ with JavaFX binaries ([Zulu](https://www.azul.com/downloads/zulu-community/?package=jdk-fx) or [Liberica](https://bell-sw.com/pages/libericajdk/) recommended)

### Build manually (without using an IDE)
If you want to run the project, simply use `gradlew run`. <br/>
To build a fat jar, use `gradlew shadowJar`. <br/>
To build executable binaries, use `gradlew jpackage` ([see distribution guideline](distribution/DISTRIBUTION_GUIDELINE.md))

## Used third-party libraries
* [Jump to list](USED_LIBRARIES.md)
* ...or you can view them in the `Boomega Info`:<br>
  ![Viewing third-party libraries in the app](img/BoomegaThirdPartyInfo.png)


## Support

If you like this project, give a :star:!

## :page_facing_up: License
This software is licensed under the [GNU General Public License v3](https://en.wikipedia.org/wiki/GNU_General_Public_License).