package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.Objects;
import java.util.Set;

/**
 * A Theme can change the appearance of GUI elements.
 *
 * <p>
 * A Theme consists of two main components: a global {@link ThemeApplier} and a
 * custom {@link ThemeApplier}. The global-themeApplier should be activated on
 * every GUI-element in the application. The custom-themeApplier is also activated on the
 * elements by default; but this behaviour can controlled by implementing the {@link Themeable}
 * interface. These {@link ThemeApplier} objects are defined by the concrete implementations
 * of the {@link Theme}.
 *
 * <p>
 * The two main Theme subclasses are the {@link LightTheme} and the {@link DarkTheme}
 * but other {@link Theme} implementations can be loaded from plugins, through the
 * {@link PluginClassLoader}.<br>
 * If we want to collect all the available {@link Theme} implementations we can use the
 * {@link Theme#getAllAvailableThemes()} method.
 * <br>
 * <p>
 * We can set the default theme by the static {@link #setDefault(Theme)} method.
 * <br>
 * The {@link Theme#applyDefault(Scene)}, {@link Theme#applyDefault(Parent)},
 * {@link Theme#applyDefault(Themeable)} methods can be used for applying the default
 * theme on the particular component.
 *
 * @author Daniel Gyorffy
 * @see ThemeApplier
 * @see Themeable
 */
public abstract class Theme {

    /**
     * Holds the current default theme
     */
    private static Theme defaultTheme;

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    protected Theme() {
        this.globalApplier = getGlobalApplier();
        this.customApplier = getCustomApplier();
    }

    protected abstract ThemeApplier getGlobalApplier();

    protected abstract ThemeApplier getCustomApplier();

    public void apply(@NotNull Themeable themeable) {
        themeable.handleThemeApply(globalApplier, customApplier);
    }

    public void apply(@NotNull Scene scene) {
        customApplier.applyBack(scene);
        globalApplier.applyBack(scene);
        customApplier.apply(scene);
        globalApplier.apply(scene);
    }

    public void apply(@NotNull Parent parent) {
        customApplier.applyBack(parent);
        globalApplier.applyBack(parent);
        customApplier.apply(parent);
        globalApplier.apply(parent);
    }

    /**
     * Collects all subtypes of the {@link Theme} class from the class-path.
     *
     * @return the set of class-references
     * @see Reflections#getSubTypesOf(Class)
     */
    public static Set<Class<? extends Theme>> getAllAvailableThemes() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addClassLoaders(ClassLoader.getSystemClassLoader(), PluginClassLoader.getInstance()));
        return reflections.getSubTypesOf(Theme.class);
    }

    /**
     * Gives access to the default theme object. If
     * the default theme is null then it will set the default theme to
     * a {@link LightTheme} instance.
     *
     * @return the default theme
     */
    public static Theme getDefault() {
        if (Objects.isNull(defaultTheme))
            defaultTheme = new LightTheme();
        return defaultTheme;
    }

    public static void setDefault(@NotNull Theme theme) {
        Theme.defaultTheme = theme;
    }

    public static void applyDefault(Themeable themeable) {
        getDefault().apply(themeable);
    }

    public static void applyDefault(Scene scene) {
        getDefault().apply(scene);
    }

    public static void applyDefault(Parent parent) {
        getDefault().apply(parent);
    }
}
