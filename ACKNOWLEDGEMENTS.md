# Acknowledgements

* [Used third-party software](#used-libraries)
    * [Used libraries](#used-libraries)
    * [Used libraries/frameworks for testing](#used-librariesframeworks-for-testing)
    * [Used Gradle plugins](#used-gradle-plugins)
* [Other acknowledgements](#other-acknowledgements)
    * [Application icon](#application-icon)
    * [Wallpapers](#wallpapers)
    * [Fonts](#fonts)

## Used third-party software

### Used libraries

| Software                                                                           | License                                                                                                                                                                 | Used by subcomponent(s)                                     |
|:-----------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------|
| [Apache Commons IO](http://commons.apache.org/proper/commons-io/)                  | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `utils`                                                     |
| [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)             | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [Apache POI](https://poi.apache.org)                                               | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `export`                                                    |
| [AnimateFX](https://github.com/Typhon0/AnimateFX)                                  | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui` `gui:utils`                                           |
| [Caffeine](https://github.com/ben-manes/caffeine)                                  | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       | 
| [ControlsFX](https://github.com/controlsfx/controlsfx)                             | [BSD 3-Clause License](https://github.com/controlsfx/controlsfx/blob/master/license.txt)                                                                                | `app` `export` `gui:utils` `gui`                            |
| [ValidatorFX](https://github.com/effad/ValidatorFX/)                               | [BSD 3-Clause License](https://github.com/effad/ValidatorFX/blob/master/LICENSE)                                                                                        | `database` `gui:utils` `gui`                                |
| [FontAwesomeFX](https://bitbucket.org/Jerady/fontawesomefx/src/master/)            | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui:utils` `gui`                                           | 
| [FXTaskbarProgressbar](http://github.com/dansoftowner/fxtaskbarprogressbar)        | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [GestureFX](https://github.com/tom91136/GestureFX)                                 | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [Guice](https://github.com/google/guice)                                           | [Apache 2.0](https://github.com/google/guice/blob/master/COPYING)                                                                                                       | `di` - and it's usages                                      |                                                                  |
| [Gson](https://github.com/google/gson)                                             | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `export` `gui` `i18n` `preferences` `rest` `update` `utils` |
| [Hibernate](https://hibernate.org/)                                                | [GNU Library or Lesser General Public License version 2.0](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html)                                                     | `database`                                                  |
| [JavaFX](https://openjfx.io/)                                                      | [GPL v2 + Classpath](http://openjdk.java.net/legal/gplv2+ce.html)                                                                                                       | `app` `export` `gui:*` `gui` `plugin`                       |
| [Jetbrains annotations](https://github.com/JetBrains/java-annotations)             | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `*`                                                         |
| [Kotlin](https://kotlinlang.org/)                                                  | [Apache 2.0](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt)                                                                                       | `*`                                                         |
| [jHTML2Md](https://github.com/nico2sh/jHTML2Md)                                    | -                                                                                                                                                                       | `boomega-gui`                                               |
| [jUserDirectories](https://github.com/Dansoftowner/jUserDirectories)               | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `utils`                                                     |
| [jSystemThemeDetector](https://github.com/Dansoftowner/jSystemThemeDetector)       | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [JMetro](https://pixelduke.com/java-javafx-theme-jmetro/)                          | [New BSD license](http://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29) | `gui`                                                       |
| [JUnique](http://www.sauronsoftware.it/projects/junique/)                          | LGPL                                                                                                                                                                    | `app`                                                       |
| [Logback Project](http://logback.qos.ch/)                                          | [EPL v1.0 and LGPL 2.1](http://logback.qos.ch/license.html)                                                                                                             | `app`                                                       |
| [MarkdownEditorControlFX](https://github.com/Dansoftowner/MarkdownEditorControlFX) | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [MDFX](https://github.com/JPro-one/markdown-javafx-renderer)                       | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |
| [MySQL Connector/J](https://mvnrepository.com/artifact/mysql/mysql-connector-java) | [GPL 2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.html)                                                                                                       | `database`                                                  |
| [Nitrite Database](https://github.com/nitrite/nitrite-java)                        | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `database`                                                  |
| [NSMenuFX](https://github.com/0x4a616e/NSMenuFX)                                   | [BSD-3-Clause License](https://opensource.org/licenses/BSD-3-Clause)                                                                                                    | `gui`                                                       |
| [OSHI](https://github.com/oshi/oshi)                                               | [MIT License](https://opensource.org/licenses/MIT)                                                                                                                      | `gui` `utils`                                               |
| [OkHttp](https://square.github.io/okhttp/)                                         | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `rest`                                                      |
| [RichTextFX](https://github.com/FXMisc/RichTextFX)                                 | [BSD-2-Clause License](https://github.com/FXMisc/RichTextFX/blob/master/LICENSE)                                                                                        | `gui`                                                       |
| [SLF4J API](http://www.slf4j.org/)                                                 | [MIT License](https://opensource.org/licenses/MIT)                                                                                                                      | `*`                                                         |
| [Version Compare](https://github.com/G00fY2/version-compare)                       | [Apache 2.0](https://github.com/G00fY2/version-compare/blob/master/LICENSE)                                                                                             | `update`                                                    |
| [WorkbenchFX](https://github.com/dlsc-software-consulting-gmbh/WorkbenchFX)        | [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)                                                                                                               | `gui`                                                       |

### Used libraries/frameworks for testing

| Software                                  | License                                                                                     |
|-------------------------------------------|---------------------------------------------------------------------------------------------|
| [JUnit 5](https://junit.org/junit5/)      | [Eclipse Public License - v 2.0](https://github.com/junit-team/junit5/blob/main/LICENSE.md) |
| [AssertJ](https://assertj.github.io/doc/) | [Apache-2.0 License](https://github.com/assertj/assertj-core/blob/main/LICENSE.txt)         |
| [Mockito](https://site.mockito.org/)      | [MIT License](https://github.com/mockito/mockito/blob/release/3.x/LICENSE)                  |

### Used Gradle plugins

| Software                                                                            | License                                                                                   |
|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| [Shadow](https://github.com/johnrengelman/shadow)                                   | [Apache 2.0](https://github.com/johnrengelman/shadow/blob/master/LICENSE)                 |
| [gradle-versions-plugin](https://github.com/ben-manes/gradle-versions-plugin)       | [Apache 2.0](https://github.com/ben-manes/gradle-versions-plugin/blob/master/LICENSE.txt) |
| [jpackage-gradle-plugin](https://github.com/petr-panteleyev/jpackage-gradle-plugin) | [BSD-2-Clause License](https://github.com/petr-panteleyev/jpackage-gradle-plugin)         |

## Other acknowledgements

### Application icon

The application icon is made by **Barnabás Joó** (📷: [@barnabas.joo](https://www.instagram.com/barnabas.joo)).

### Wallpapers

* [Wallpaper House](http://wallpaper-house.com)
* [PassMater](https://www.deviantart.com/passmater)
* [Freepik](https://www.freepik.com/)

### Fonts

* [Jetbrains Mono](https://www.jetbrains.com/lp/mono/)
* [Poppins](https://fonts.google.com/specimen/Poppins)