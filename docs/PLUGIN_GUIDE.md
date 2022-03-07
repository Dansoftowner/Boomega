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

## API

The root of the Boomega plugin hierarchy is:
[`com.dansoftware.boomega.plugin.api.BoomegaPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/BoomegaPlugin.kt).

All other plugin classes should implement this interface.

**BoomegaPlugin subtypes:**

* [`LanguagePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/LanguagePlugin.kt) - for adding a new language support
* [`ThemePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ThemePlugin.kt) - for adding a new UI theme
* [`RecordExporterPlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/RecordExporterPlugin.kt) - for adding new record exporting option
* [`ModulePlugin`](/src/main/java/com/dansoftware/boomega/plugin/api/ModulePlugin.kt) - for adding new UI modules

**The DisabledPlugin annotation**

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


