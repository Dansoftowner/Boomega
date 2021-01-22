package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.gui.theme.applier.ThemeApplier;
import com.dansoftware.libraryapp.locale.I18N;
import com.jthemedetecor.OsThemeDetector;
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

    private static final ThemeMeta<OsSynchronizedTheme> THEME_META =
            new ThemeMeta<>(OsSynchronizedTheme.class, () -> I18N.getGeneralValue("theme.sync"), InternalThemeDesigner.INSTANCE);

    static {
        registerTheme(THEME_META);
    }

    private final Consumer<Boolean> osThemeListener;

    private final OsThemeDetector osThemeDetector;
    private volatile Theme currentTheme;
    private final Theme darkTheme;
    private final Theme lightTheme;

    public OsSynchronizedTheme() {
        this.osThemeListener = new SyncFunction(this);
        this.osThemeDetector = OsThemeDetector.getDetector();
        this.osThemeDetector.registerListener(osThemeListener);
        this.darkTheme = new DarkTheme();
        this.lightTheme = new LightTheme();
        this.currentTheme = getCurrentTheme();
    }

    @NotNull
    @Override
    protected ThemeApplier getApplier() {
        return currentTheme.getApplier();
    }

    private Theme getCurrentTheme() {
        return this.osThemeDetector.isDark() ? darkTheme : lightTheme;
    }

    @Override
    protected void onThemeDropped() {
        osThemeDetector.removeListener(osThemeListener);
    }



    private static final class SyncFunction implements Consumer<Boolean> {

        private final OsSynchronizedTheme synchTheme;

        SyncFunction(@NotNull OsSynchronizedTheme synchTheme) {
            this.synchTheme = synchTheme;
        }

        @Override
        public void accept(Boolean isDark) {
            Platform.runLater(() -> {
                if (isDark) {
                    synchTheme.currentTheme = synchTheme.darkTheme;
                    synchTheme.update(synchTheme.lightTheme);
                } else {
                    synchTheme.currentTheme = synchTheme.lightTheme;
                    synchTheme.update(synchTheme.darkTheme);
                }
            });
        }
    }
}
