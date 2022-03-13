# Plugin guide

> Incomplete and not reliable yet!

#### List of contents

* [Intro](#intro)
  * [How is it work](#how-is-it-work)
* [The plugin directory](#the-plugin-directory)
* [API overview](#api-overview)
  * [Plugin lifecycle](#plugin-lifecycle)
* [Setting up a plugin project](#setting-up-a-plugin-project)
* [Plugin development tutorials & examples](#plugin-development-tutorials--examples)
  * [Language plugins](#language-plugins)
  * [Theme plugins](#theme-plugins)
  * [Record exporting plugins](#record-exporting-plugins)
  * [Module plugins](#module-plugins)

# Intro

Boomega allows you to develop plugins for the app in order to add more features/functionality to the app.  
Plugins can be written both in **java** and **kotlin**.

> You can view the loaded plugins and their impact on the app in the [plugin manager](/docs/USER_GUIDE.md#plugin-manager).

### How is it work

On the JVM (Java Virtual Machine) it's possible to load classes dynamically at runtime with the help of Class Loaders.  
Since Boomega is a JVM application, it takes full advantage of this.

# The plugin directory

Boomega automatically loads plugin archives (`.jar`s) from the **default plugin directory**.

* On **Windows**: `<User directory>\AppData\Roaming\Dansoftware\boomega\plugin`
* On **Linux** & **MacOS**: `<User directory>/boomega/plugin`

If you want to load your plugin into Boomega, you should place the jar file into this directory.

> You can open the plugin directory from Boomega:
> **File > Open plugin directory**

# API overview

The root of the Boomega plugin hierarchy is:
[`com.dansoftware.boomega.plugin.api.BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt).

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) - for adding support for a new language
* [`ThemePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ThemePlugin.kt) - for adding a new UI theme
* [`RecordExporterPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/RecordExporterPlugin.kt) - for adding new record exporting option
* [`ModulePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ModulePlugin.kt) - for adding new UI modules

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

Every [`BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt) should provide this information:
* `name`: `String` - defines the plugin's name
* `author`: [`Person`](/src/main/java/com/dansoftware/boomega/util/Person.kt) - information about the plugin's author
* `version`: `String` - defines the plugin's version
* _Optional_: `icon`: [`Image`](https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/image/Image.html) - the plugin's icon (as  a JavaFX image)

## Plugin lifecycle

When the application starts running, it searches for all `BoomegaPlugin` implementations. After a `BoomegaPlugin` is instantiated,
Boomega invokes the `init()` method on it. 
**Every plugin class will be instantiated only once during the application lifetime**, meaning they behave practically as 
singletons.
When the application shuts down, the `destroy()` method is invoked on the plugin instance.

# Setting up a plugin project

The recommended build system to be used for a plugin project is `Gradle`.

## Including the plugin-kit dependency

> Not available yet

## Running test application

For testing your plugins in action, you can run a test application instance easily.

> Not available yet

## Building your archive

If your plugin project doesn't include additional dependencies, you can use the basic gradle `jar` task.

### Handling dependencies

If your plugin requires dependencies, you might use an additional gradle plugin like [Shadow](https://github.com/johnrengelman/shadow)
for building a fat jar. But in this case you have to make sure that the fat jar doesn't include the Boomega
(plugin-kit) binaries, because it might reduce performance when Boomega loads the plugin.

An alternative solution is to simply place the dependency jars also into the plugin directory.

> Note: Don't include dependencies already present in Boomega itself.   
> You can view the dependencies used by Boomega in the [acknowledgements](/ACKNOWLEDGEMENTS.md#used-libraries).

# Plugin development tutorials & examples

## Language plugins

If you want to add a new language to Boomega through a plugin, you can implement the
[`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) interface.

> If you want to contribute a language to be included in the core Boomega itself, look at [this issue](https://github.com/Dansoftowner/Boomega/issues/162).

Firstly, create your own `.properties` file that contains the translations. 
View the [default resource file](/src/main/resources/com/dansoftware/boomega/i18n/Values.properties) to have an idea.

After you've done this, you have to create your [LanguagePack](/src/main/java/com/dansoftware/boomega/i18n/LanguagePack.java).
A `LanguagePack` in Boomega provides the `ResourceBundle` (representing the .properties file), 
a `Collator` (for defining the alphabetical order) and other things needed for defining a language. 

A simple example:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class PortugueseLanguagePack : LanguagePack(Locale("pt"), AUTHOR) {

    // The com/mypackage/MyValues_pt.properties file
    override fun getValues(): ResourceBundle = super.getBundle("com.mypackage.MyValues")

    override fun isRTL(): Boolean = false // Portuguese is not a right-to-left language

    companion object {
        // represents the person who translated the language
        private val AUTHOR = Person(
            lastName = "LastName",
            firstName = "FirstName",
            email = "myemail@example.com"
        )
    }
}
```

</td>

<td>

```java
public class PortugueseLanguagePack extends LanguagePack {

    // the Locale representing the language we want to translate to (in this case Portuguese)
    private static final Locale LOCALE = new Locale("pt");

    // represents the person who translated the language
    private static final Person AUTHOR = new Person("LastName", "FirstName", "myemail@example.com");

    protected PortugueseLanguagePack() {
        super(LOCALE, AUTHOR);
    }

    @Override
    public @NotNull ResourceBundle getValues() {
        // The com/mypackage/MyValues_pt.properties file
        return super.getBundle("com.mypackage.MyValues");
    }

    @Override
    protected boolean isRTL() {
        return false; // Portuguese is not a right-to-left language
    }
}
```

</td>

</tr>
</table>

Finally, you can implement the `LanguagePlugin` interface:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class PortugueseLanguagePlugin : LanguagePlugin {

    // Here you have to return your LanguagePack
    override val languagePack get() = PortugueseLanguagePack()
   
    override val name: String = "Portuguese language plugin"
    override val author = Person(firstName = "FirstName", lastName = "LastName", "myemail@example.com")
    override val version: String = "1.0.0"
    override val description: String? get() = null
    
    override fun init() { ... }
    override fun destroy() { ... }
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

You can view the internal LanguagePack implementations (to understand the concept better)
in the [`com.dansoftware.boomega.i18n`](/src/main/java/com/dansoftware/boomega/i18n) package e.g:
* [EnglishLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/EnglishLanguagePack.java)
* [HungarianLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/HungarianLanguagePack.java)
* [TurkishLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/TurkishLanguagePack.java)

## Theme plugins

## Record exporting plugins

## Module plugins