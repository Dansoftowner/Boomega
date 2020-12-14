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

class MainView extends BorderPane implements ContextTransformable, Themeable {

    private final Context asContext;
    private final Database database;

    private final MenuBar menuBar;
    private final MainContentView contentView;

    MainView(@NotNull Database database, @NotNull Preferences preferences, @NotNull DatabaseTracker tracker) {
        this.asContext = Context.from(contentView = new MainContentView());
        this.database = database;
        this.menuBar = new MenuBar(asContext, preferences, tracker);
        this.setTop(menuBar);
        Theme.registerThemeable(this);
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.getCustomApplier().applyBack(this);
        newTheme.getCustomApplier().apply(this);
    }
}
