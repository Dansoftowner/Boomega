package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

public interface ThemeApplier {
    void apply(@NotNull Scene scene);
    void apply(@NotNull Parent parent);

    void applyBack(@NotNull Scene scene);
    void applyBack(@NotNull Parent parent);
}
