# Plugin guide

> Incomplete and not reliable yet!

#### List of contents

* [Intro](#intro)
    * [How is it work](#how-is-it-work)
* [The plugin directory](#the-plugin-directory)
* [API overview](#api-overview)
    * [Plugin lifecycle](#plugin-lifecycle)
    * [Required plugin metadata](#required-plugin-metadata)
* [Setting up a plugin project](#setting-up-a-plugin-project)
* [Plugin development tutorials & examples](#plugin-development-tutorials--examples)
    * [Language plugins](#language-plugins)
      * [The LanguagePlugin interface](#the-languageplugin-interface)
    * [Theme plugins](#theme-plugins)
      * [The ThemePlugin interface](#the-themeplugin-interface)
    * [Module plugins](#module-plugins)
      * [The ModulePlugin interface](#the-moduleplugin-interface)
    * [Record exporting plugins](#record-exporting-plugins)
    * [Database provider plugins](#database-provider-plugins)

# Intro

Boomega allows you to develop plugins in order to add more features/functionality to the app.  
Plugins can be written in both **java** and **kotlin**.   

The recommended language for writing plugins is **kotlin** because most of the Plugin
API entities are written in it. This guide will provide you both kotlin and java examples tough.

> You can view the loaded plugins and their impact on the app.
> Read about the [plugin manager](/docs/USER_GUIDE.md#plugin-manager) in the user guide.

### How is it work?

On the JVM (Java Virtual Machine) it's possible to load classes dynamically at runtime with the help
of [Class Loaders](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html).  
Since Boomega is a JVM application, it takes full advantage of this.

# The plugin directory

Boomega automatically loads plugin archives (`.jar`s) from the **default plugin directory**.

* Path on **Windows**: `%APPDATA%\Dansoftware\boomega\plugin`
* Path on **Linux** & **macOS**: `<User directory>/boomega/plugin`

If you want to load your plugin into Boomega, you should place the jar file into this directory.

> You can open the plugin directory from the Boomega menu-bar:
> **File > Open plugin directory**

# API overview

The root of the Boomega plugin hierarchy is:
[`com.dansoftware.boomega.plugin.api.BoomegaPlugin`](/boomega-plugin/src/main/kotlin/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt)
.

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`com.dansoftware.boomega.plugin.LanguagePlugin`](/boomega-i18n/src/main/kotlin/com/dansoftware/boomega/plugin/LanguagePlugin.kt) - for adding support for a new
  language
* [`com.dansoftware.boomega.plugin.ThemePlugin`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/plugin/ThemePlugin.kt) - for adding a new UI theme
* [`com.dansoftware.boomega.plugin.RecordExporterPlugin`](/boomega-export/src/main/kotlin/com/dansoftware/boomega/plugin/RecordExporterPlugin.kt) - for adding new
  record exporting option
* [`com.dansoftware.boomega.plugin.ModulePlugin`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/plugin/ModulePlugin.kt) - for adding new UI modules
* [`com.dansoftware.boomega.plugin.DatabaseProviderPlugin`](/boomega-database/src/main/kotlin/com/dansoftware/boomega/plugin/DatabaseProviderPlugin.kt) - for adding custom database support

**The DisabledPlugin annotation:**

The [`@DisabledPlugin`](/boomega-plugin/src/main/kotlin/com/dansoftware/boomega/plugin/api/DisabledPlugin.java) annotation can be used
for preventing Boomega to load a plugin class.

Example:
<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
@DisabledPlugin // make Boomega ignore this plugin
class MonokaiThemePlugin : ThemePlugin {
    ...
}
```

</td>

<td>

```java

@DisabledPlugin // make Boomega ignore this plugin
public class MonokaiThemePlugin implements ThemePlugin {
    ...
}
```

</td>

</tr>
</table>

## Plugin lifecycle

When the application starts running, it searches for all `BoomegaPlugin` implementations and instantiates them. **Every
plugin class will be instantiated only once during the application lifetime**, meaning they behave practically as
singletons. After a `BoomegaPlugin` is instantiated, Boomega invokes the `init()` method on it. When the application
shuts down, the `destroy()` method is invoked on the plugin instance.

## Required plugin metadata

Every [`BoomegaPlugin`](/boomega-plugin/src/main/kotlin/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt) should provide this
information:

* `name`: `String` - defines the plugin's name
* `author`: [`Person`](/boomega-utils/src/main/kotlin/com/dansoftware/boomega/util/Person.kt) - information about the plugin's author
* `version`: `String` - defines the plugin's version
* _Optional_: `icon`: [`Image`](https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/image/Image.html) - the
  plugin's icon (as a JavaFX image)

Example:
<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class MonokaiThemePlugin : ThemePlugin {

  // General BoomegaPlugin meta-data
  override val name: String = "Mononokai Theme Plugin"
  override val author = Person(firstName = "FirstName", lastName = "LastName", "myemail@example.com")
  override val version: String = "1.0.0"
  override val description: String? = "Adds a Monokai Theme scheme."

  ...
}
```

</td>

<td>

```java
public class MonokaiThemePlugin implements ThemePlugin {
  
  @Override
  public @NotNull String getName() {
        return "Monokai Theme Plugin";
  }
  
  @Override
  public @NotNull Person getAuthor() {
        return new Person("LastName", "FirstName", "myemail@example.com");
  }
  
  @Override
  public @NotNull String getVersion() {
        return "1.0.0";
  }
  
  @Override
  public @Nullable String getDescription() {
        return "Adds a Monokai Theme scheme.";
  }
  
  ...
}
```

</td>

</tr>
</table>

# Setting up a plugin project

**Requirements:**
- JDK 17 (recommended: [OpenJDK](https://jdk.java.net/17/))
- Recommended build system: Gradle
- Recommended IDE: [Intellj Idea](https://www.jetbrains.com/idea/)

## Including the plugin-kit dependency

> Not available yet

## Running test application

For testing your plugins in action, you can run a test application instance easily.

> Not available yet

## Building your archive

If your plugin project doesn't include additional dependencies, you can use the basic gradle `jar` task.

### Handling dependencies

If your plugin requires dependencies, you might use an additional gradle plugin
like [Shadow](https://github.com/johnrengelman/shadow)
for building a fat jar. But in this case you have to make sure that the fat jar doesn't include the Boomega
(plugin-kit) binaries, because it might reduce performance when Boomega loads the plugin.

An alternative solution is to simply place the dependency jars also into the plugin directory.

> Note: Don't include dependencies already present in Boomega itself.   
> You can view the dependencies used by Boomega in the [acknowledgements](/ACKNOWLEDGEMENTS.md#used-libraries).

# Plugin development tutorials & examples

## Language plugins

Before we start, make sure you have read the [language guide](dev/LANGUAGE_GUIDE.md) that helps you understand the basic concepts.

### The `LanguagePlugin` interface

To make your [LanguagePack](dev/LANGUAGE_GUIDE.md#the-languagepack-class) recognized by Boomega you have to supply it in your 
[LanguagePlugin](/boomega-i18n/src/main/kotlin/com/dansoftware/boomega/plugin/LanguagePlugin.kt) implementation:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class PortugueseLanguagePlugin : LanguagePlugin {

    ...
  
    // Here you have to return your LanguagePack
    override val languagePack get() = PortugueseLanguagePack()
  
}
```

</td>

<td>

```java
public class PortugueseLanguagePlugin implements LanguagePlugin {

    ...
    
    @NotNull
    @Override
    public LanguagePack getLanguagePack() {
        return new PortugueseLanguagePack();
    }
    
}
```

</td>

</tr>
</table>

## Theme plugins

Before we start, make sure you have read the [theme guide](dev/THEME_GUIDE.md) that helps you understand the basic concepts.

### The `ThemePlugin` interface

To make Boomega recognize your theme, you have to implement
the [`ThemePlugin`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/plugin/ThemePlugin.kt) interface and provide your previously 
created [Theme](dev/THEME_GUIDE.md#the-theme-class).

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class NordThemePlugin : ThemePlugin {
    ...
    
    // Here, you have to return your instantiated theme
    override val theme get() = NordTheme()
}
```

</td>

<td>

```java
public class NordThemePlugin implements ThemePlugin {
    ...
    
    @NotNull
    @Override
    public Theme getTheme() {
        // Here, you have to return your instantiated theme
        return new NordTheme();
    }
}
```

</td>

</tr>
</table>

## Module plugins

Read the [ui module guide](dev/UI_MODULE_GUIDE.md) before you go forward. 

### The `ModulePlugin` interface

To make Boomega recognize the plugin, you have to implement the [`ModulePlugin`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/plugin/ModulePlugin.kt) interface
where you supply your newly created [Module](dev/UI_MODULE_GUIDE.md#the-module-class):

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class HelloModule : ModulePlugin {
    // Here you have to supply your module's instance
    override fun getModule(context: Context, database: Database): Module = HelloModule()
}
```

</td>

<td>

```java
public class HelloModule extends ModulePlugin {
    @NotNull
    @Override
    public Module getModule(@NotNull Context context, @NotNull Database database) {
        // Here you have to supply your module's instance
        return new HelloModule();
    }
}
```

</td>

</tr>
</table>

You might have noticed that the `getModule` method takes two arguments:
- context: [`Context`](/boomega-gui/api/src/main/kotlin/com/dansoftware/boomega/gui/api/Context.kt) - you can use it for interacting with the UI (making alerts, notifications, etc..)
- database: [`Database`](/boomega-database/src/main/kotlin/com/dansoftware/boomega/database/api/Database.kt) - the object can be used to communicate with the opened database; might be just read-only

If your module doesn't need these, you can simply ignore them.

## Record exporting plugins

Read the [record export guide](dev/RECORD_EXPORT_GUIDE.md) before you go forward.

### The `RecordExporterPlugin` interface

If you want to add your record-exporter through a plugin, you have to implement
the [RecordExporterPlugin](/boomega-export/src/main/kotlin/com/dansoftware/boomega/plugin/RecordExporterPlugin.kt) interface.

#### Example plugin
Look at the [txt-table-export-plugin](https://github.com/boomegapp/txt-table-export-plugin), a record export plugin allows
to export records into text tables.

## Database provider plugins

Read the [databases guide](dev/DATABASES_GUIDE.md) before you continue.

> Documentation in progress