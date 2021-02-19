package com.dansoftware.boomega.gui.theme.applier;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class AndThemeApplier implements ThemeApplier {

    private final ThemeApplier[] subAppliers;

    public AndThemeApplier(ThemeApplier... subAppliers) {
        this.subAppliers = subAppliers;
    }

    @Override
    public void apply(@NotNull Scene scene) {
        Stream.of(subAppliers).forEach(subApplier -> subApplier.apply(scene));
    }

    @Override
    public void apply(@NotNull Parent parent) {
        Stream.of(subAppliers).forEach(subApplier -> subApplier.apply(parent));
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        Stream.of(subAppliers).forEach(subApplier -> subApplier.applyBack(scene));
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        Stream.of(subAppliers).forEach(subApplier -> subApplier.applyBack(parent));
    }
}
