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
      * [Specifying the alphabetical order](#specifying-the-alphabetical-order)
    * [Theme plugins](#theme-plugins)
      * [Stylesheets](#stylesheets)
      * [The Theme class](#the-theme-class)
      * [The ThemePlugin interface](#the-themeplugin-interface)
    * [Record exporting plugins](#record-exporting-plugins)
    * [Module plugins](#module-plugins)

# Intro

Boomega allows you to develop plugins in order to add more features/functionality to the app.  
Plugins can be written both in **java** and **kotlin**.   

The recommended language for writing plugins is **kotlin** because most of the Plugin
API entities are written in it. Although, this guide will provide you both kotlin and java examples. 

> You can view the loaded plugins and their impact on the app in the [plugin manager](/docs/USER_GUIDE.md#plugin-manager).

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
[`com.dansoftware.boomega.plugin.api.BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt)
.

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) - for adding support for a new
  language
* [`ThemePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ThemePlugin.kt) - for adding a new UI theme
* [`RecordExporterPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/RecordExporterPlugin.kt) - for adding new
  record exporting option
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
- Gradle
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

You are free to contribute a new language to be included in the core Boomega itself (look
at [this issue](https://github.com/Dansoftowner/Boomega/issues/162) for more help).  
However, you can also add a new language as a separate plugin by implementing the
[`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) interface.

Firstly, create your own `.properties` file that contains the translations. View
the [default resource file](/src/main/resources/com/dansoftware/boomega/i18n/Values.properties) to have an idea.

After you've done this, you have to create
your [LanguagePack](/src/main/java/com/dansoftware/boomega/i18n/LanguagePack.java). A `LanguagePack` in Boomega provides
the `ResourceBundle` (representing the .properties file)
and other things needed for defining a language.

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
    public @NotNull
    ResourceBundle getValues() {
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

**Finally, you can implement the `LanguagePlugin` interface:**

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

### Specifying the alphabetical order

Knowing the alphabetical order for Boomega is crucial for several features (e.g. sorting records in a table-view).  
Defining `ABC`s in Java is possible with the help of 
[Collators](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/Collator.html).
By overriding the `LanguagePack.getABCCollator()` method you can specify the `Collator` for your language-pack.  
*If you don't specify any collator for your pack, the default collator will be used which is `Collator.getInstance()`.*

Look at this simplified snippet from the internal [HungarianLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/HungarianLanguagePack.java):
```java
public class HungarianLanguagePack extends LanguagePack {

  ...
    
  @Override
  public @NotNull Collator getABCCollator() {
    return new NullHandlingCollator(new ABCCollator());
  }

  ...
  
  private static final class ABCCollator extends RuleBasedCollator {
    ABCCollator() throws ParseException {
      super("""
            < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS \
            < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J \
            < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó \
            < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T \
            < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS\
            """
      );
    }
  }
}
```

Notice that it wraps the base collator into a [NullHandlingCollator](/src/main/java/com/dansoftware/boomega/i18n/NullHandlingCollator.kt) 
for preventing possible null-pointer exceptions when comparing `null` values with the collator in the future. You should 
also follow this practice in your own language-pack.

### Other examples

You can view the internal LanguagePack implementations (for understanding the concepts better)
in the [`com.dansoftware.boomega.i18n`](/src/main/java/com/dansoftware/boomega/i18n) package e.g:

* [EnglishLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/EnglishLanguagePack.java)
* [HungarianLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/HungarianLanguagePack.java)
* [TurkishLanguagePack](/src/main/java/com/dansoftware/boomega/i18n/TurkishLanguagePack.java)

## Theme plugins

### Stylesheets
For creating your custom themes, you should have knowledge in [JavaFX CSS](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/doc-files/cssref.html).
The Boomega UI elements have unique style-classes and identifiers. Look at the internal Boomega style-sheets to get in touch:
* [base.css](/src/main/resources/com/dansoftware/boomega/gui/theme/base.css) - global UI style configuration (used in both the `light` and `dark` theme)
* [light.css](/src/main/resources/com/dansoftware/boomega/gui/theme/light.css) - the light-theme styles. Used by [`LightTheme`](/src/main/java/com/dansoftware/boomega/gui/theme/LightTheme.kt)
* [dark.css](/src/main/resources/com/dansoftware/boomega/gui/theme/dark.css) - the dark-theme styles. Used by [`DarkTheme`](/src/main/java/com/dansoftware/boomega/gui/theme/DarkTheme.kt)

Note that these stylesheets are partial, because these internal themes use the [JMetro JavaFX Theme](https://pixelduke.com/java-javafx-theme-jmetro/)
as a basis. So if you write your stylesheets from scratch you may have to work more.

### The `Theme` class

In Boomega, a [`Theme`](/src/main/java/com/dansoftware/boomega/gui/theme/Theme.kt) is responsible for applying the styles
on the UI (usually by simply adding the stylesheets to the JavaFX elements).

Methods need to be implemented:
* `apply(Scene)` - should apply the styles on a JavaFX [Scene](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Scene.html)
* `apply(Parent)` - should apply the styles on a JavaFX [Parent](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Parent.html)

Also, the theme should also provide a way to "reset" the UI:
* `deApply(Scene)` - should remove the styles from a JavaFX [Scene](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Scene.html)
* `deApply(Parent)` - should remove the styles from a JavaFX [Parent](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Parent.html)

Optional:
* `init()` - executed when the Theme is set as `default`
* `destroy()` - executed when the Theme is not default anymore

A simple example:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
...
import com.dansoftware.boomega.util.res

class NordTheme : Theme() {

  override val name: String = "Nord theme"

  // Path of the css file located next to this class (lot of ways to resolve the path)
  private val styleSheet: String = res("nord.css", NordTheme::class)!!.toExternalForm()

  override fun apply(scene: Scene) {
    scene.stylesheets.add(styleSheet)
  }

  override fun apply(region: Parent) {
    region.stylesheets.add(styleSheet)
  }

  override fun deApply(scene: Scene) {
    scene.stylesheets.remove(styleSheet)
  }

  override fun deApply(region: Parent) {
    region.stylesheets.remove(styleSheet)
  }
}
```

</td>

<td>

```java
public class NordTheme extends Theme {

  // Path of the css file located next to this class (lot of ways to resolve the path)
  private static final String STYLESHEET =
          NordTheme.class.getResource("nord.css").toExternalForm();

  @NotNull
  @Override
  public String getName() {
    return "Nord theme";
  }

  @Override
  public void apply(@NotNull Scene scene) {
    scene.getStylesheets().add(STYLESHEET);
  }

  @Override
  public void apply(@NotNull Parent region) {
    region.getStylesheets().add(STYLESHEET);
  }

  @Override
  public void deApply(@NotNull Scene scene) {
    scene.getStylesheets().remove(STYLESHEET);
  }

  @Override
  public void deApply(@NotNull Parent region) {
    region.getStylesheets().remove(STYLESHEET);
  }
}
```

</td>

</tr>
</table>

#### Other examples
You can view the internal `Theme` implementations (for understanding the concepts better)
in the [`com.dansoftware.boomega.gui.theme`](/src/main/java/com/dansoftware/boomega/gui/theme) package e.g:

* [DarkTheme](/src/main/java/com/dansoftware/boomega/gui/theme/DarkTheme.kt)
* [LightTheme](/src/main/java/com/dansoftware/boomega/gui/theme/LightTheme.kt)
* [OsSynchronizedTheme](/src/main/java/com/dansoftware/boomega/gui/theme/OsSynchronizedTheme.kt)

You are free to contribute your own theme to be included in the core Boomega itself. However, if you want to add your
theme as a plugin, let's go to the next section!

### The `ThemePlugin` interface

To make Boomega recognize your theme, you have to implement
the [`ThemePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ThemePlugin.kt) interface and provide your newly 
created `Theme`.

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


## Record exporting plugins

## Module plugins