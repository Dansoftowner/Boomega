package com.dansoftware.libraryapp.gui.theme.detect;

import com.dansoftware.libraryapp.util.OsInfo;
import com.registry.RegistryKey;
import com.registry.RegistryValue;
import com.registry.RegistryWatcher;
import com.registry.event.RegistryListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * For detecting the theme (dark/light) used by the Operating System.
 *
 * @author Daniel Gyorffy
 */
public abstract class OsThemeDetector {

    private static OsThemeDetector osThemeDetector;

    private OsThemeDetector() {
    }

    public static synchronized OsThemeDetector getDetector() {
        if (osThemeDetector != null) {
            return osThemeDetector;
        } else if (OsInfo.isWindows10()) {
            return osThemeDetector = new WindowsThemeDetector();
        } else {
            return osThemeDetector = new EmptyDetector();
        }
    }

    /**
     * Returns that the os using a dark or a light theme.
     *
     * @return {@code true} if the os uses dark theme; {@code false} otherwise.
     */
    public abstract boolean isDark();

    /**
     * Registers a {@link Consumer} that will listen to a theme-change.
     *
     * @param darkThemeListener the {@link Consumer} that accepts a {@link Boolean} that represents
     *                          that the os using a dark theme or not
     */
    public abstract void registerListener(Consumer<Boolean> darkThemeListener);

    /**
     * Removes the listener.
     */
    public abstract void removeListener(Consumer<Boolean> darkThemeListener);

    /**
     * Determines the dark/light theme by the windows registry values through the JRegistry API.
     */
    private static final class WindowsThemeDetector extends OsThemeDetector {

        private static final String REGISTRY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
        private static final String REGISTRY_VALUE = "AppsUseLightTheme";

        private final Map<Consumer<Boolean>, RegistryListener> listeners;
        private final RegistryKey registryPath;

        private WindowsThemeDetector() {
            this.registryPath = new RegistryKey(REGISTRY_PATH);
            this.listeners = Collections.synchronizedMap(new HashMap<>());
        }

        @Override
        public boolean isDark() {
            RegistryValue registryValue = registryPath.getValue(REGISTRY_VALUE);
            if (registryValue != null) {
                byte[] byteData = registryValue.getByteData();
                if (byteData.length > 0) {
                    int value = byteData[0];
                    return value == 0;
                }
            }
            return false;
        }

        @Override
        public void registerListener(Consumer<Boolean> darkThemeListener) {
            RegistryListener registryListener = registryEvent -> {
                RegistryKey key = registryEvent.getKey();
                if (key.equals(registryPath)) {
                    darkThemeListener.accept(isDark());
                }
            };
            RegistryWatcher.addRegistryListener(registryListener);
            RegistryWatcher.watchKey(registryPath);
            listeners.put(darkThemeListener, registryListener);
        }

        @Override
        public void removeListener(Consumer<Boolean> darkThemeListener) {
            RegistryListener removed = listeners.remove(darkThemeListener);
            RegistryWatcher.removeRegistryListener(removed);
            if (listeners.isEmpty()) RegistryWatcher.removeKey(registryPath);
        }
    }

    private static final class EmptyDetector extends OsThemeDetector {
        @Override
        public boolean isDark() {
            return false;
        }

        @Override
        public void registerListener(Consumer<Boolean> darkThemeListener) {
        }

        @Override
        public void removeListener(Consumer<Boolean> darkThemeListener) {
        }
    }
}
