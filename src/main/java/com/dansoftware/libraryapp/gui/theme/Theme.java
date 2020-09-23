package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.IdentifiableWeakReference;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
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
 *
 * <p>
 * We can set the default theme by the static {@link #setDefault(Theme)} method.
 *
 * <p>
 * We can register {@link Themeable} objects through the static {@link #registerThemeable(Themeable)} method.
 * Every time when a new theme is set as default every registered {@link Themeable} object will be notified.
 * <i><b>Kind of an Observable-Observer pattern</b></i>
 *
 * <br>
 * The {@link Theme#applyDefault(Scene)}, {@link Theme#applyDefault(Parent)} methods can be used for applying the default
 * theme on the particular component.
 *
 * @author Daniel Gyorffy
 * @see ThemeApplier
 * @see Themeable
 */
public abstract class Theme {

    private static final Set<WeakReference<Themeable>> THEMEABLE_SET = new HashSet<>();

    /**
     * Holds the current default theme
     */
    private static Theme defaultTheme;

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    protected Theme() {
        this.globalApplier = createGlobalApplier();
        this.customApplier = createCustomApplier();
    }

    /**
     * Every {@link Theme} implementation should create a global {@link ThemeApplier}
     * through this method.
     *
     * <p>
     * The created {@link ThemeApplier} will be cached.
     */
    @NotNull
    protected abstract ThemeApplier createGlobalApplier();

    /**
     * Every {@link Theme} implementation can create a custom {@link ThemeApplier}
     * through this method.
     *
     * <p>
     * The created {@link ThemeApplier} will be cached.
     */
    @NotNull
    protected ThemeApplier createCustomApplier() {
        return ThemeApplier.empty();
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

    public ThemeApplier getGlobalApplier() {
        return globalApplier;
    }

    public ThemeApplier getCustomApplier() {
        return customApplier;
    }

    public static synchronized void registerThemeable(@NotNull Themeable themeable) {
        if (THEMEABLE_SET.add(new IdentifiableWeakReference<>(themeable))) {
            themeable.handleThemeApply(defaultTheme);
        }
    }

    private static void notifyThemeableInstances(@NotNull Theme newTheme) {
        for (Iterator<WeakReference<Themeable>> iterator = THEMEABLE_SET.iterator(); iterator.hasNext(); ) {
            WeakReference<Themeable> themeableWeakReference = iterator.next();
            Themeable themeableRef = themeableWeakReference.get();
            if (themeableRef == null) iterator.remove();
            else themeableRef.handleThemeApply(newTheme);
        }
    }

    public static synchronized void setDefault(@NotNull Theme theme) {
        if (theme != defaultTheme) {
            Theme.defaultTheme = theme;
            notifyThemeableInstances(theme);
        }
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

    public static void applyDefault(Scene scene) {
        getDefault().apply(scene);
    }

    public static void applyDefault(Parent parent) {
        getDefault().apply(parent);
    }
}
