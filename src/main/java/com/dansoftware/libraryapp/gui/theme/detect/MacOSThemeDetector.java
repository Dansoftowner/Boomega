package com.dansoftware.libraryapp.gui.theme.detect;

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
    public void registerListener(Consumer<Boolean> darkThemeListener) {
    }

    @Override
    public void removeListener(Consumer<Boolean> darkThemeListener) {
    }
}
