# Plugin guide

> Incomplete and not reliable yet!

## Intro

Boomega allows you to develop plugins for the app in order to add features/expand the functionality to/of the app.  
Plugins can be written both in **java** and **kotlin**.

## The plugin directory

Boomega automatically loads plugin archives (`.jar`s) from the **default plugin directory**.

* On **Windows**: `<User directory>\AppData\Roaming\Dansoftware\boomega\plugin`
* On **Linux** & **MacOS**: `<User directory>/boomega/plugin`

If you want to load your plugin archive, you should place the file in this directory.

> You can view the loaded plugins and their impact on the app in the [plugin manager](/docs/USER_GUIDE.md#plugin-manager). 

## API overview

The root of the Boomega plugin hierarchy is:
[`com.dansoftware.boomega.plugin.api.BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt).

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) - for adding a new language support
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

> Your plugin classes will be instantiated only once during the application lifetime.

## Setting up a plugin project

The recommended build system to be used for a plugin project is `Gradle`.

### Including the plugin-kit dependency

> Not available yet

### Running test application

For testing your plugins in action, you can run a test application instance easily.

> Not available yet

### Building your archive

If your plugin project doesn't include additional dependencies, you can use the basic gradle `jar` task.

#### Handling dependencies

If your plugin requires dependencies, you might use an additional gradle plugin like [Shadow](https://github.com/johnrengelman/shadow)
for building a fat jar. But in this case you have to make sure that the fat jar doesn't include the Boomega
(plugin-kit) binaries, because it might reduce performance when Boomega loads the plugin.

An alternative solution is to simply place the dependency jars also into the plugin directory.

> Note: Don't include dependencies already present in Boomega itself.   
> You can view the dependencies used by Boomega in the [acknowledgements](/ACKNOWLEDGEMENTS.md#used-libraries).

## Plugin development tutorials & examples

### Language plugins

### Theme plugins

### Record exporting plugins

### Module plugins