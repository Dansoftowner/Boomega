package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.gui.theme.detect.OsThemeDetector;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * A {@link Theme} implementation that synchronizes the App's theme
 * with the System UI Theme.
 *
 * @author Daniel Gyorffy
 */
public class OsSynchronizedTheme extends Theme {

    private static final Logger logger = LoggerFactory.getLogger(OsSynchronizedTheme.class);

    private final Consumer<Boolean> osThemeListener;

    private final OsThemeDetector osThemeDetector;
    private final Theme darkTheme;

    private final Theme lightTheme;
    public OsSynchronizedTheme() {
        this.osThemeListener = new SyncFunction(this);
        this.osThemeDetector = OsThemeDetector.getDetector();
        this.osThemeDetector.registerListener(osThemeListener);
        this.darkTheme = new DarkTheme();
        this.lightTheme = new LightTheme();
    }

    private Theme getCurrentTheme() {
        return this.osThemeDetector.isDark() ? darkTheme : lightTheme;
    }

    @Override
    protected void onThemeDropped() {
        osThemeDetector.removeListener(osThemeListener);
    }

    @Override
    public @NotNull ThemeApplier getGlobalApplier() {
        return getCurrentTheme().getGlobalApplier();
    }

    @Override
    public @NotNull ThemeApplier getCustomApplier() {
        return getCurrentTheme().getCustomApplier();
    }

    private static final class SyncFunction implements Consumer<Boolean> {

        private final OsSynchronizedTheme synchTheme;

        SyncFunction(@NotNull OsSynchronizedTheme synchTheme) {
            this.synchTheme = synchTheme;
        }

        @Override
        public void accept(Boolean isDark) {
            Platform.runLater(() -> {
                if (isDark) synchTheme.update(synchTheme.lightTheme);
                else synchTheme.update(synchTheme.darkTheme);
            });
        }
    }
}
