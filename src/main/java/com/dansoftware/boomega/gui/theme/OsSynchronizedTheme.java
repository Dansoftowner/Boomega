package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.i18n.I18N;
import com.jthemedetecor.OsThemeDetector;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private Theme getCurrentTheme() {
        return this.osThemeDetector.isDark() ? darkTheme : lightTheme;
    }

    @Override
    protected void onThemeDropped() {
        logger.debug("OsSynchronizedTheme dropped!");
        logger.debug("Removing os theme listener...");
        osThemeDetector.removeListener(osThemeListener);
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        currentTheme.applyBack(scene);
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        currentTheme.applyBack(parent);
    }

    @Override
    public void apply(@NotNull Scene scene) {
        currentTheme.apply(scene);
    }

    @Override
    public void apply(@NotNull Parent parent) {
        currentTheme.apply(parent);
    }

    public static ThemeMeta<OsSynchronizedTheme> getMeta() {
        return new ThemeMeta<>(OsSynchronizedTheme.class, () -> I18N.getValue("app.ui.theme.sync"), InternalThemeDesigner.INSTANCE);
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
