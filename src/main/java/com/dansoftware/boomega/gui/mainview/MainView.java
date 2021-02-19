package com.dansoftware.boomega.gui.mainview;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainView extends BorderPane implements ContextTransformable {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final MainActivity activity;
    private final Database database;

    private final MenuBarBase menuBar;
    private final MainContentView contentView;

    MainView(@NotNull MainActivity activity,
             @NotNull Database database,
             @NotNull Preferences preferences,
             @NotNull DatabaseTracker tracker) {
        this.activity = Objects.requireNonNull(activity);
        this.contentView = new MainContentView(preferences, database);
        this.database = database;
        this.menuBar = new MenuBarBase(new AppMenuBar(contentView.getContext(), this, preferences, tracker));
        this.setTop(menuBar);
        this.setCenter(contentView);
    }

    public MainContentView getContentView() {
        return this.contentView;
    }

    @Override
    public @NotNull Context getContext() {
        return contentView.getContext();
    }

    public DatabaseMeta getOpenedDatabase() {
        return database.getMeta();
    }

    private static final class MenuBarBase extends StackPane {
        private static final String STYLE_CLASS = "menu-bar-base";

        MenuBarBase(@NotNull javafx.scene.control.MenuBar menuBar) {
            getChildren().add(menuBar);
            getStyleClass().add(STYLE_CLASS);
        }
    }
}
