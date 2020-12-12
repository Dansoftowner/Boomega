package com.dansoftware.libraryapp.gui.theme.detect;

import com.registry.RegistryKey;
import com.registry.RegistryValue;
import com.registry.RegistryWatcher;
import com.registry.event.RegistryListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Determines the dark/light theme by the windows registry values through the JRegistry API.
 *
 * @author Daniel Gyorffy
 */
class WindowsThemeDetector extends OsThemeDetector {
    private static final String REGISTRY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
    private static final String REGISTRY_VALUE = "AppsUseLightTheme";

    private final Map<Consumer<Boolean>, RegistryListener> listeners;
    private final RegistryKey registryPath;

    WindowsThemeDetector() {
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
    public void registerListener(@NotNull Consumer<Boolean> darkThemeListener) {
        RegistryListener registryListener = registryEvent -> {
            RegistryKey key = registryEvent.getKey();
            if (key.equals(registryPath)) {
                darkThemeListener.accept(isDark());
            }
        };
        RegistryWatcher.addRegistryListener(registryListener);
        if (listeners.isEmpty()) RegistryWatcher.watchKey(registryPath);
        listeners.put(darkThemeListener, registryListener);
    }

    @Override
    public void removeListener(@Nullable Consumer<Boolean> darkThemeListener) {
        RegistryListener removed = listeners.remove(darkThemeListener);
        RegistryWatcher.removeRegistryListener(removed);
        if (listeners.isEmpty()) RegistryWatcher.removeKey(registryPath);
    }
}
