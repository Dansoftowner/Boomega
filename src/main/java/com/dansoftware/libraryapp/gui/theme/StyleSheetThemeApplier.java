package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

public class StyleSheetThemeApplier implements ThemeApplier {

    private final String styleSheetPath;

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
