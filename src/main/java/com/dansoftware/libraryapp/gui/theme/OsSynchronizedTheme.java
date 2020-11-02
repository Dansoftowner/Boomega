package com.dansoftware.libraryapp.gui.theme;

import com.jthemedetecor.SystemUIThemeDetector;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class OsSynchronizedTheme extends Theme {

    private static final Logger logger = LoggerFactory.getLogger(OsSynchronizedTheme.class);

    private final Consumer<Boolean> osThemeListener = isDark -> super.update();

    public OsSynchronizedTheme() {
        logger.debug("Constructor called!");
        SystemUIThemeDetector.getDetector().registerListener(osThemeListener);
    }

    private Theme getCurrentTheme() {
        return SystemUIThemeDetector.getDetector().isDark() ?
                new DarkTheme() : new LightTheme();
    }

    @Override
    protected void onApplyBack() {
        SystemUIThemeDetector.getDetector().removeListener(osThemeListener);
    }

    @Override
    protected @NotNull ThemeApplier createGlobalApplier() {
        return getCurrentTheme().getGlobalApplier();
    }

    @Override
    protected @NotNull ThemeApplier createCustomApplier() {
        return getCurrentTheme().getCustomApplier();
    }

    private class DynamicApplier implements ThemeApplier {

        @Override
        public void apply(@NotNull Scene scene) {
            OsSynchronizedTheme.this.getCurrentTheme().apply(scene);
        }

        @Override
        public void apply(@NotNull Parent parent) {
            OsSynchronizedTheme.this.getCurrentTheme().apply(parent);
        }

        @Override
        public void applyBack(@NotNull Scene scene) {
            //OsSynchronizedTheme.this.getCurrentTheme()(parent);
        }

        @Override
        public void applyBack(@NotNull Parent parent) {

        }
    }
}
