package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.util.ReflectionUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class JMetroThemeApplier implements ThemeApplier {

    private static final List<String> J_METRO_STYLE_SHEETS;

    static {
        J_METRO_STYLE_SHEETS = getJMetroStyleSheets();
    }

    /**
     * Returns the jmetro stylesheet-paths through reflection.
     *
     * @return the {@link List} of stylesheets
     */
    private static List<String> getJMetroStyleSheets() {
        try {
            List<Field> fields = List.of(
                    JMetro.class.getDeclaredField("BASE_STYLESHEET_URL"),
                    JMetro.class.getDeclaredField("PANES_STYLESHEET_URL"),
                    JMetro.class.getDeclaredField("BASE_EXTRAS_STYLESHEET_URL"),
                    JMetro.class.getDeclaredField("BASE_OTHER_LIBRARIES_STYLESHEET_URL"),
                    Style.class.getDeclaredField("DARK_STYLE_SHEET_URL"),
                    Style.class.getDeclaredField("LIGHT_STYLE_SHEET_URL")
            );

            return fields.stream()
                    .map(ReflectionUtils::getDeclaredStaticValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private final Style jMetroStyle;

    public JMetroThemeApplier(@NotNull Style jMetroStyle) {
        this.jMetroStyle = jMetroStyle;
    }

    @Override
    public void apply(@NotNull Scene scene) {
        new JMetro(jMetroStyle).setScene(scene);
    }

    @Override
    public void apply(@NotNull Parent parent) {
        new JMetro(jMetroStyle).setParent(parent);
    }

    @Override
    public void applyBack(@NotNull Scene scene) {
        scene.getStylesheets().removeAll(J_METRO_STYLE_SHEETS);
    }

    @Override
    public void applyBack(@NotNull Parent parent) {
        parent.getStylesheets().removeAll(J_METRO_STYLE_SHEETS);
    }
}
