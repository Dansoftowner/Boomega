package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link StyleSheetThemeApplier} is a {@link ThemeApplier} that
 * can apply css-style resources on GUI elements.
 *
 * @author Daniel Gyorffy
 */
public class StyleSheetThemeApplier implements ThemeApplier {

    private final String styleSheetPath;

    /**
     * Creates a normal {@link StyleSheetThemeApplier}.
     *
     * @param styleSheetPath the stylesheet path
     */
    public StyleSheetThemeApplier(@NotNull String styleSheetPath) {
        this.styleSheetPath = styleSheetPath;
    }

    @Override
    public void apply(@NotNull Scene scene) {
        scene.getStylesheets().add(styleSheetPath);
    }

    @Override
    public void apply(@NotNull Parent parent) {
        parent.getStylesheets().add(styleSheetPath);
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        scene.getStylesheets().remove(styleSheetPath);
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        parent.getStylesheets().remove(styleSheetPath);
    }
}
