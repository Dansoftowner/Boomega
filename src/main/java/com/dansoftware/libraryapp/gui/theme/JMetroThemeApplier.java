package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

public class JMetroThemeApplier implements ThemeApplier {

    private final JMetro jMetro;

    public JMetroThemeApplier(@NotNull Style jMetroStyle) {
        this.jMetro = new JMetro(jMetroStyle);
    }

    @Override
    public void apply(@NotNull Scene scene) {

    }

    @Override
    public void apply(@NotNull Parent parent) {

    }

    @Override
    public void applyBack(@NotNull Scene scene) {

    }

    @Override
    public void applyBack(@NotNull Parent parent) {

    }
}
