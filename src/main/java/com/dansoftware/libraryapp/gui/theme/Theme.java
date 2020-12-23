package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.IdentifiableWeakReference;
import com.dansoftware.libraryapp.util.ReflectionUtils;
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

    private static final Set<WeakReference<Themeable>> THEMEABLE_SET = new HashSet<>();
    private static final Map<Class<? extends Theme>, ThemeMeta<? extends Theme>> registeredThemes = new HashMap<>();

    /**
     * Holds the current default theme
     */
    private static Theme defaultTheme;

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
        registeredThemes.put(themeMeta.getThemeClass(), themeMeta);
    }

    public static Set<Class<? extends Theme>> getAvailableThemes() {
        return registeredThemes.keySet();
    }

    public static Collection<ThemeMeta<? extends Theme>> getAvailableThemesData() {
        return registeredThemes.values();
    }

    protected Theme() {
    }

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

    public void applyBack(@NotNull Scene scene,
                          @NotNull ThemeApplier customApplier,
                          @NotNull ThemeApplier globalApplier) {
        customApplier.applyBack(scene);
        globalApplier.applyBack(scene);
    }

    public void applyBack(@NotNull Parent parent,
                          @NotNull ThemeApplier customApplier,
                          @NotNull ThemeApplier globalApplier) {
        customApplier.applyBack(parent);
        globalApplier.applyBack(parent);
    }

    protected void applyBack(@NotNull Scene scene) {
        applyBack(scene, getCustomApplier(), getGlobalApplier());
    }

    public void applyBack(@NotNull Parent parent) {
        applyBack(parent, getCustomApplier(), getGlobalApplier());
    }

    public void apply(@NotNull Scene scene) {
        ThemeApplier customApplier = getCustomApplier();
        ThemeApplier globalApplier = getGlobalApplier();
        applyBack(scene, customApplier, globalApplier);
        customApplier.apply(scene);
        globalApplier.apply(scene);
    }

    public void apply(@NotNull Parent parent) {
        ThemeApplier customApplier = getCustomApplier();
        ThemeApplier globalApplier = getGlobalApplier();
        applyBack(parent, customApplier, globalApplier);
        customApplier.apply(parent);
        globalApplier.apply(parent);
    }

    public abstract ThemeApplier getGlobalApplier();

    public abstract ThemeApplier getCustomApplier();

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
        if (defaultTheme != null) {
            if (!defaultTheme.getClass().isInstance(theme)) {
                defaultTheme.onThemeDropped();
                notifyThemeableInstances(Theme.defaultTheme, theme);
                Theme.defaultTheme = theme;
            }
        } else {
            notifyThemeableInstances(new EmptyTheme(), theme);
            Theme.defaultTheme = theme;
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
        if (defaultTheme == null) {
            defaultTheme = DefaultThemeFactory.INSTANCE.get();
        }
        return defaultTheme;
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
        public ThemeApplier getGlobalApplier() {
            return ThemeApplier.empty();
        }

        @Override
        public ThemeApplier getCustomApplier() {
            return ThemeApplier.empty();
        }
    }
}
