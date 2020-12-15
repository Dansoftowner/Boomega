package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class MainView extends BorderPane implements ContextTransformable, Themeable {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final MainActivity activity;
    private final Context asContext;
    private final Database database;

    private final MenuBar menuBar;
    private final MainContentView contentView;

    MainView(@NotNull MainActivity activity,
             @NotNull Database database,
             @NotNull Preferences preferences,
             @NotNull DatabaseTracker tracker) {
        this.activity = Objects.requireNonNull(activity);
        this.asContext = Context.from(contentView = new MainContentView());
        this.database = database;
        this.menuBar = new MenuBar(asContext, preferences, tracker);
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
        oldTheme.getCustomApplier().applyBack(this.menuBar);
        newTheme.getCustomApplier().apply(this.menuBar);
        oldTheme.getGlobalApplier().applyBack(this.contentView);
        newTheme.getGlobalApplier().apply(this.contentView);
    }
}
