package com.dansoftware.libraryapp.gui.theme.detect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Determines the dark/light theme on Mac System.
 *
 * <b>DOESN'T WORKS YET</b>
 *
 * @author Daniel Gyorffy
 */
public class MacOSThemeDetector extends OsThemeDetector {
    @Override
    public boolean isDark() {
        return false;
    }

    @Override
    public void registerListener(@NotNull Consumer<Boolean> darkThemeListener) {
    }

    @Override
    public void removeListener(@Nullable Consumer<Boolean> darkThemeListener) {
    }
}
