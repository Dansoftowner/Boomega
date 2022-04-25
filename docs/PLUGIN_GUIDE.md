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
      * [Property files](#property-files) 
      * [The LanguagePack class](#the-languagepack-class) 
        * [Specifying the alphabetical order](#specifying-the-alphabetical-order)
      * [The LanguagePlugin interface](#the-languageplugin-interface)
    * [Theme plugins](#theme-plugins)
      * [Stylesheets](#stylesheets)
      * [The Theme class](#the-theme-class)
      * [The ThemePlugin interface](#the-themeplugin-interface)
    * [Module plugins](#module-plugins)
      * [What are modules in Boomega?](#what-are-modules-in-boomega)
      * [The Module class](#the-module-class)
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
[`BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt)
.

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) - for adding support for a new
  language
* [`ThemePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ThemePlugin.kt) - for adding a new UI theme
* [`RecordExporterPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/RecordExporterPlugin.kt) - for adding new
  record exporting option
* [`ModulePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ModulePlugin.kt) - for adding new UI modules
* [`DatabaseProviderPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/DatabaseProviderPlugin.kt) - for adding custom database support

**The DisabledPlugin annotation:**

The [`@DisabledPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/DisabledPlugin.java) annotation can be used
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

Every [`BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt) should provide this
information:

* `name`: `String` - defines the plugin's name
* `author`: [`Person`](/src/main/java/com/dansoftware/boomega/util/Person.kt) - information about the plugin's author
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

### What are modules in Boomega?

A `Module` in Boomega provides a particular UI 
area where specific tasks can be achieved.

On the Boomega Home Screen, each module has its own `tile` displayed there:

![Tiles on the Boomega Home Screen](/docs/img/pluginguide/module_plugins/ModulesOnBoomegaHomeScreen.jpg)

As you can see, the `Records-View` and the `Google Books Import View` are present in Boomega as modules.

And when you click on a tile, it opens a tab for the module's content:
![Tabs displayed for modules](/docs/img/pluginguide/module_plugins/TabsOpenedForModules.jpg)

**You have the ability to add your custom modules to the app by plugins.**

### The `Module` class

If you want to create your own, you should extend the [`Module`](/src/main/java/com/dansoftware/boomega/gui/databaseview/Module.kt) class.

The members you must override are:

* `name`: `String` - The user-visible name of the module
* `icon`: [`Node`](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Node.html) - The javafx node that serves as a symbol/icon for the module
* `id`: `String` - a unique identifier of the module
* `buildContent()`: [`Node`](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Node.html) - The actual UI content the module provides
* `destroy()`: `Boolean` - gets called when the user closes the module (by closing its `tab`); if it returns `false` the user won't be able to 

Simple example:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class HelloModule : Module() {
    override val id: String = "hello-module"
    override val name: String = "Hello"
    override val icon: Node get() = MaterialDesignIconView(MaterialDesignIcon.EMOTICON_HAPPY)

    override fun buildContent(): Node = StackPane(Label("Hello"))
    override fun destroy(): Boolean = true
}
```

</td>

<td>

```java
public class HelloModule extends Module {

    @NotNull
    @Override
    public String getId() {
        return "hello-module";
    }

    @NotNull
    @Override
    public String getName() {
        return "Hello";
    }

    @NotNull
    @Override
    public Node getIcon() {
        return new MaterialDesignIconView(MaterialDesignIcon.EMOTICON_HAPPY);
    }

    @NotNull
    @Override
    protected Node buildContent() {
        return new StackPane(new Label("Hello"));
    }

    @Override
    protected boolean destroy() {
        return true;
    }
}
```

</td>

</tr>
</table>

We've just created a module that will display the text "Hello" on the screen.
But we have one more step to take.

### The `ModulePlugin` interface

To make Boomega recognize the plugin, you have to implement the [`ModulePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ModulePlugin.kt) interface:

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
- context: [`Context`](/src/main/java/com/dansoftware/boomega/gui/api/Context.kt) - you can use it for interacting with the UI (making alerts, notifications, etc..)
- database: [`Database`](/src/main/java/com/dansoftware/boomega/database/api/Database.kt) - the object can be used to communicate with the opened database; might be just read-only

If your module doesn't need these, you can simply ignore them.

---
**Result:**  
![Result on the home screen](/docs/img/pluginguide/module_plugins/ResultOnHomeScreen.png)   
![Result opened](/docs/img/pluginguide/module_plugins/ResultOpened.png)

## Record exporting plugins

> Incomplete and not reliable because the API is not reliable yet

Boomega allows users to export their [Record](/src/main/java/com/dansoftware/boomega/database/api/data/Record.kt)s into
some output formats (like JSON and Excel spreadsheet). The list options are expandable by writing plugins.

### The `RecordExporter` interface

The entities directly responsible for exporting records into a specific format
are [RecordExporter](/src/main/java/com/dansoftware/boomega/export/api/RecordExporter.kt)s.

Simple example:


### The `RecordExporterPlugin` interface

If you want to add your record-exporter through a plugin, you have to implement
the [RecordExporterPlugin](/src/main/java/com/dansoftware/boomega/plugin/api/RecordExporterPlugin.kt) interface.

Here you have to supply your previously created record-exporter.

Simple example:

## Database provider plugins