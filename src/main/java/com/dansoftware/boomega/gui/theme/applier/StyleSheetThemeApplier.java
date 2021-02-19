package com.dansoftware.boomega.gui.theme.applier;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A {@link StyleSheetThemeApplier} is a {@link ThemeApplier} that
 * can apply css-style resources on GUI elements.
 *
 * @author Daniel Gyorffy
 */
public class StyleSheetThemeApplier implements ThemeApplier {

    private final List<String> styleSheets;

    /**
     * Creates a normal {@link StyleSheetThemeApplier}.
     *
     * @param styleSheets List of stylesheet paths
     */
    public StyleSheetThemeApplier(@NotNull List<String> styleSheets) {
        this.styleSheets = styleSheets;
    }

    @Override
    public void apply(@NotNull Scene scene) {
        scene.getStylesheets().addAll(styleSheets);
    }

    @Override
    public void apply(@NotNull Parent parent) {
        parent.getStylesheets().addAll(styleSheets);
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        scene.getStylesheets().removeAll(styleSheets);
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        parent.getStylesheets().removeAll(styleSheets);
    }
}
