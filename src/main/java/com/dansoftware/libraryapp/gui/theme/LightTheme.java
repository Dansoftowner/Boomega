package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

/**
 * A LightTheme is a {@link Theme} implementation that applies a light theme.
 * It's also considered to be the default application theme.
 *
 * <p>
 * The global {@link ThemeApplier} of this theme should be used for every gui-view in the application;
 * the custom {@link ThemeApplier} applies JMetro's light style on the particular element;
 * it may not be used for every gui-object.
 *
 * @author Daniel Gyorffy
 */
public class LightTheme extends Theme {

    private static final String GLOBAL_LIGHT_STYLE_SHEET = "/com/dansoftware/libraryapp/gui/theme/global-light.css";

    public LightTheme() {
        super();
    }

    @Override
    protected ThemeApplier getGlobalApplier() {
        return new StyleSheetThemeApplier(GLOBAL_LIGHT_STYLE_SHEET);
    }

    @Override
    protected ThemeApplier getCustomApplier() {
        return new ThemeApplier() {
            @Override
            public void apply(@NotNull Scene scene) {
                new JMetro(Style.LIGHT).setScene(scene);
            }

            @Override
            public void apply(@NotNull Parent parent) {
                new JMetro(Style.LIGHT).setParent(parent);
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
    }
}
