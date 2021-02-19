package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.gui.theme.applier.ThemeApplier;
import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.dansoftware.boomega.util.IdentifiableWeakReference;
import com.dansoftware.boomega.util.ReflectionUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;

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
 * {@link Theme#getAvailableThemes()} method.
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

    private static final Logger logger = LoggerFactory.getLogger(Theme.class);

    private static final Set<WeakReference<Themeable>> THEMEABLE_SET =
            Collections.synchronizedSet(new HashSet<>());

    private static final Map<Class<? extends Theme>, ThemeMeta<? extends Theme>> REGISTERED_THEMES =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Holds the current default theme
     */
    private static final ObjectProperty<Theme> defaultTheme = new SimpleObjectProperty<>();

    static {
        loadThemes();
    }

    private static void loadThemes() {
        //collecting Themes from the core project
        ReflectionUtils.getSubtypesOf(Theme.class).forEach(ReflectionUtils::initializeClass);
        //collecting Themes from plugins
        PluginClassLoader.getInstance().initializeSubtypeClasses(Theme.class);
    }

    protected static void registerTheme(@NotNull ThemeMeta<? extends Theme> themeMeta) {
        Objects.requireNonNull(themeMeta);
        REGISTERED_THEMES.put(themeMeta.getThemeClass(), themeMeta);
    }

    public static Set<Class<? extends Theme>> getAvailableThemes() {
        return REGISTERED_THEMES.keySet();
    }

    public static Collection<ThemeMeta<? extends Theme>> getAvailableThemesData() {
        return REGISTERED_THEMES.values();
    }

    protected Theme() {
    }

    @NotNull
    protected abstract ThemeApplier getApplier();

    protected void update(@NotNull Theme oldTheme) {
        notifyThemeableInstances(oldTheme, this);
    }

    protected void update() {
        update(this);
    }

    /**
     * It's executed on the particular {@link Theme} object, when a new
     * theme is set.
     */
    protected void onThemeDropped() {
    }

    public void applyBack(@NotNull Scene scene) {
        getApplier().applyBack(scene);
    }

    public void applyBack(@NotNull Parent parent) {
        getApplier().applyBack(parent);
    }

    public void apply(@NotNull Scene scene) {
        ThemeApplier applier = getApplier();
        applier.applyBack(scene);
        applier.apply(scene);
    }

    public void apply(@NotNull Parent parent) {
        ThemeApplier applier = getApplier();
        applier.applyBack(parent);
        applier.apply(parent);
    }

    public static synchronized void registerThemeable(@NotNull Themeable themeable) {
        if (THEMEABLE_SET.add(new IdentifiableWeakReference<>(themeable))) {
            themeable.handleThemeApply(new EmptyTheme(), getDefault());
        }
    }

    private static void notifyThemeableInstances(@NotNull Theme oldTheme, @NotNull Theme newTheme) {
        for (Iterator<WeakReference<Themeable>> iterator = THEMEABLE_SET.iterator(); iterator.hasNext(); ) {
            WeakReference<Themeable> themeableWeakReference = iterator.next();
            Themeable themeableRef = themeableWeakReference.get();
            if (themeableRef == null) iterator.remove();
            else themeableRef.handleThemeApply(oldTheme, newTheme);
        }
    }

    public static synchronized void setDefault(@NotNull Theme theme) {
        Objects.requireNonNull(theme);
        Theme currentTheme = defaultTheme.get();
        if (currentTheme != null) {
            if (!currentTheme.getClass().isInstance(theme)) {
                currentTheme.onThemeDropped();
                notifyThemeableInstances(currentTheme, theme);
                Theme.defaultTheme.set(theme);
            }
        } else {
            notifyThemeableInstances(new EmptyTheme(), theme);
            Theme.defaultTheme.set(theme);
        }
    }

    /**
     * Gives access to the default theme object. If
     * the default theme is null then it will set the default theme to
     * a {@link LightTheme} instance.
     *
     * @return the default theme
     */
    public static Theme getDefault() {
        if (defaultTheme.get() == null) {
            defaultTheme.set(DefaultThemeFactory.INSTANCE.get());
        }
        return defaultTheme.get();
    }

    public static void applyDefault(Scene scene) {
        getDefault().apply(scene);
    }

    public static void applyDefault(Parent parent) {
        getDefault().apply(parent);
    }

    public static Theme empty() {
        return new EmptyTheme();
    }

    private static final class EmptyTheme extends Theme {

        @Override
        protected @NotNull ThemeApplier getApplier() {
            return ThemeApplier.empty();
        }
    }
}
