package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DarkTheme extends Theme {

    private static final ThemeApplier GLOBAL_APPLIER = new ThemeApplier() {
        private final String STYLE_SHEET = "/com/dansoftware/libraryapp/gui/theme/global-dark.css";

        @Override
        public void apply(@NotNull Scene scene) {
            scene.getStylesheets().add(STYLE_SHEET);
        }

        @Override
        public void apply(@NotNull Parent parent) {
            parent.getStylesheets().add(STYLE_SHEET);
        }

        @Override
        public void applyBack(@NotNull Scene scene) {
            scene.getStylesheets().remove(STYLE_SHEET);
        }

        @Override
        public void applyBack(@NotNull Parent parent) {
            parent.getStylesheets().remove(STYLE_SHEET);
        }
    };

    private static final ThemeApplier CUSTOM_APPLIER = new ThemeApplier() {
        @Override
        public void apply(@NotNull Scene scene) {
            new JMetro(Style.DARK).setScene(scene);
        }

        @Override
        public void apply(@NotNull Parent parent) {
            new JMetro(Style.DARK).setParent(parent);
        }

        @Override
        public void applyBack(@NotNull Scene scene) {
            scene.getStylesheets().clear();
        }

        @Override
        public void applyBack(@NotNull Parent parent) {
            parent.getStylesheets().clear();
        }
    };

    public DarkTheme() {
        super(GLOBAL_APPLIER, CUSTOM_APPLIER);
    }
}
