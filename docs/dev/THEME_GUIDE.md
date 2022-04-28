# Adding custom themes to Boomega

This page will guide you how to **contribute UI Themes** to the app, and also gives the fundamentals for developing
**theme-plugins**.

## Prerequisites

**These apply to you only if you're contributing to the core project:**

* Make sure you've read the [contribution guideline](/CONTRIBUTING.md)
* Work in the [`boomega-gui`](/boomega-gui) subproject

## Stylesheets

For creating your custom themes, you should have knowledge
in [JavaFX CSS](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/doc-files/cssref.html). The Boomega UI
elements have unique style-classes and identifiers. Look at the internal Boomega style-sheets to get in touch:

* [base.css](/boomega-gui/src/main/resources/com/dansoftware/boomega/gui/theme/base.css) - global UI style
  configuration (used in both the `light` and `dark` theme); you are free to import it in your own stylesheets.
* [light.css](/boomega-gui/src/main/resources/com/dansoftware/boomega/gui/theme/light.css) - the light-theme styles.
  Used by [`LightTheme`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/LightTheme.kt)
* [dark.css](/boomega-gui/src/main/resources/com/dansoftware/boomega/gui/theme/dark.css) - the dark-theme styles. Used
  by [`DarkTheme`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/DarkTheme.kt)

Note that these stylesheets are partial, because these internal themes use
the [JMetro JavaFX Theme](https://pixelduke.com/java-javafx-theme-jmetro/)
as a basis. So if you write your stylesheets from scratch you may have to work more.

**These apply to you only if you're contributing to the core project:**

* Place your stylesheets into
  the [resources/com/dansoftware/boomega/gui/theme](/boomega-gui/src/main/resources/com/dansoftware/boomega/gui/theme)
  directory.

## The `Theme` class

In Boomega, a [`Theme`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/Theme.kt) is responsible for
applying the styles on the UI (usually by simply adding the stylesheets to the JavaFX elements).

Methods need to be implemented:

* `apply(Scene)` - should apply the styles on a
  JavaFX [Scene](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Scene.html)
* `apply(Parent)` - should apply the styles on a
  JavaFX [Parent](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Parent.html)

Also, the theme should also provide a way to "reset" the UI:

* `deApply(Scene)` - should remove the styles from a
  JavaFX [Scene](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Scene.html)
* `deApply(Parent)` - should remove the styles from a
  JavaFX [Parent](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Parent.html)

Optional:

* `init()` - executed when the `Theme` is set as `default`
* `destroy()` - executed when the `Theme` is not default anymore

**These apply to you only if you're contributing to the core project:**

* Place your theme classes into
  the [com.dansoftware.boomega.gui.theme](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme) directory.

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

### Registering your theme

**This page applies to you only if you're contributing to the core project.**

After you've created your theme implementation you have to register it's
full [class-name](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Class.html#getName()) in
the [internal_themes.json](/boomega-gui/src/main/resources/com/dansoftware/boomega/gui/theme/internal_themes.json)
config file.

Like this:

```json
{
  ...
  "themeClassNames" : [
    ...
    "com.dansoftware.boomega.gui.theme.NordTheme"
  ]
}
```

### Creating themes on top of JMetro

If you want to build you theme on top of the [JMetro JavaFX Theme](https://pixelduke.com/java-javafx-theme-jmetro/),
then you can do it easily by extending
the [JMetroTheme](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/JMetroTheme.kt) class.

E.g:

```kotlin
class ExtendedDarkTheme : JMetroTheme(Style.DARK) {
    ...
    private val styleSheet: String = ...;

    override fun apply(region: Parent) {
        super.apply(region)
        region.stylesheets.add(styleSheet)
    }

    override fun apply(scene: Scene) {
        super.apply(scene)
        scene.stylesheets.add(styleSheet)
    }

    override fun deApply(region: Parent) {
        super.deApply(region)
        region.stylesheets.remove(styleSheet)
    }

    override fun deApply(scene: Scene) {
        super.deApply(scene)
        scene.stylesheets.remove(styleSheet)
    }
}
```

### Other examples

You can view the internal `Theme` implementations (for understanding the concepts better)
in the [`com.dansoftware.boomega.gui.theme`](/boomega-gui/src/main/java/com/dansoftware/boomega/gui/theme) package e.g:

* [DarkTheme](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/DarkTheme.kt)
* [LightTheme](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/LightTheme.kt)
* [OsSynchronizedTheme](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/theme/OsSynchronizedTheme.kt)

---

You are free to contribute your own theme to be included in the core Boomega itself. However, if you want to add your
theme as a plugin, go to the [plugin guide](../PLUGIN_GUIDE.md#theme-plugins)!