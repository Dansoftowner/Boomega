package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class MainView extends BorderPane implements ContextTransformable, Themeable {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final MainActivity activity;
    private final Context asContext;
    private final Database database;

    private final MenuBarBase menuBar;
    private final MainContentView contentView;

    MainView(@NotNull MainActivity activity,
             @NotNull Database database,
             @NotNull Preferences preferences,
             @NotNull DatabaseTracker tracker) {
        this.activity = Objects.requireNonNull(activity);
        this.asContext = Context.from(contentView = new MainContentView());
        this.database = database;
        this.menuBar = new MenuBarBase(new MenuBar(asContext, database.getMeta(), preferences, tracker));
        this.setTop(menuBar);
        this.setCenter(contentView);
        Theme.registerThemeable(this);
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this.menuBar);
        newTheme.apply(this.menuBar);
        oldTheme.getGlobalApplier().applyBack(this.contentView);
        newTheme.getGlobalApplier().apply(this.contentView);
    }

    private static final class MenuBarBase extends StackPane {
        private static final String STYLE_CLASS = "menu-bar-base";

        MenuBarBase(@NotNull javafx.scene.control.MenuBar menuBar) {
            getChildren().add(menuBar);
            getStyleClass().add(STYLE_CLASS);
        }
    }
}
