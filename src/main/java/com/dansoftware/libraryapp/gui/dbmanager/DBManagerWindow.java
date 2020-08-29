package com.dansoftware.libraryapp.gui.dbmanager;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * A DBManagerWindow is used for displaying a {@link DBManagerView} in a window.
 */
public class DBManagerWindow extends Stage {

    private static final double WIDTH = 1000;
    private static final double HEIGHT = 430;

    /**
     * Creates the {@link DBManagerWindow}.
     *
     * @param view  the {@link DBManagerView} to display; mustn't be null
     * @param owner the owner-window; may be null
     */
    public DBManagerWindow(@NotNull DBManagerView view, @Nullable Window owner) {
        this.setScene(new Scene(view));
        this.initModality(Modality.APPLICATION_MODAL);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.centerOnScreen();
        Theme.applyDefault((Themeable) view);
    }
}
