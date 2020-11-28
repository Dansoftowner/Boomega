package com.dansoftware.libraryapp.gui.window.undecorate.control;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class TitledPane extends BorderPane implements Themeable {

    private static final String STYLE_CLASS = "internalTitledPane";
    private final TitleBar titleBar;

    public TitledPane(@NotNull Stage stage, @NotNull Node center) {
        super(center);
        Theme.registerThemeable(this);
        this.getStyleClass().add(STYLE_CLASS);
        this.titleBar = new TitleBar(stage);
        this.setTop(titleBar);
    }

    public TitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.getGlobalApplier().applyBack(this);
        newTheme.getGlobalApplier().apply(this);
    }
}
