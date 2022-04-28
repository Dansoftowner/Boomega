# Adding custom UI Modules to Boomega

This page will show you what are Modules in Boomega and guide you how to contribute new UI-Modules for Boomega + gives
the fundamentals for developing module-plugins.

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

If you want to create your own, you should extend
the [`com.dansoftware.boomega.gui.databaseview.Module`](/boomega-gui/src/main/kotlin/com/dansoftware/boomega/gui/databaseview/Module.kt)
class.

The members you must override are:

* `name`: `String` - The user-visible name of the module
* `icon`: [`Node`](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Node.html) - The javafx node that serves
  as a symbol/icon for the module
* `id`: `String` - a unique identifier of the module
* `buildContent()`: [`Node`](https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/Node.html) - The actual UI
  content the module provides
* `destroy()`: `Boolean` - gets called when the user closes the module (by closing its `tab`); if it returns `false` the
  user won't be able to

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

**If you're developing your module as a plugin, look at the [plugin guide](../PLUGIN_GUIDE.md#module-plugins) for
further instructions!**

---
**Result:**

On the Home Page:

![Result on the home screen](/docs/img/pluginguide/module_plugins/ResultOnHomeScreen.png)

After opened:

![Result opened](/docs/img/pluginguide/module_plugins/ResultOpened.png)