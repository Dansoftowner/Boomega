/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.dansoftware.boomega.plugin.Plugins;
import com.dansoftware.boomega.plugin.api.ThemePlugin;
import com.dansoftware.boomega.util.IdentifiableWeakReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Theme can change the appearance of GUI elements.
 *
 * <p>
 * The two main Theme subclasses are the {@link LightTheme} and the {@link DarkTheme}
 * but other {@link Theme} implementations can be loaded from plugins, see
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
 * @author Daniel Gyorffy
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
        registerThemes();
    }

    protected Theme() {
    }

    protected void update(@NotNull Theme oldTheme) {
        if (getDefault().equals(this))
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

    public abstract void applyBack(@NotNull Scene scene);

    public abstract void applyBack(@NotNull Parent parent);

    public abstract void apply(@NotNull Scene scene);

    public abstract void apply(@NotNull Parent parent);

    private static void registerThemes() {
        registerBaseThemes();
        registerPluginThemes();
    }

    private static void registerBaseThemes() {
        REGISTERED_THEMES.put(DarkTheme.class, DarkTheme.getMeta());
        REGISTERED_THEMES.put(LightTheme.class, LightTheme.getMeta());
        REGISTERED_THEMES.put(OsSynchronizedTheme.class, OsSynchronizedTheme.getMeta());
    }

    private static void registerPluginThemes() {
        logger.debug("Checking plugins for themes...");
        Plugins.getInstance().of(ThemePlugin.class).stream()
                .map(ThemePlugin::getThemeMeta)
                .peek(it -> logger.debug("Found theme: '{}'", it.getThemeClass()))
                .forEach(it -> REGISTERED_THEMES.put(it.getThemeClass(), it));
    }

    public static Set<Class<? extends Theme>> getAvailableThemes() {
        return REGISTERED_THEMES.keySet();
    }

    public static Collection<ThemeMeta<? extends Theme>> getAvailableThemesData() {
        return REGISTERED_THEMES.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
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

    public static Theme empty() {
        return new EmptyTheme();
    }

    private static final class EmptyTheme extends Theme {
        @Override
        public void applyBack(@NotNull Scene scene) {
        }

        @Override
        public void applyBack(@NotNull Parent parent) {
        }

        @Override
        public void apply(@NotNull Scene scene) {
        }

        @Override
        public void apply(@NotNull Parent parent) {
        }
    }
}
