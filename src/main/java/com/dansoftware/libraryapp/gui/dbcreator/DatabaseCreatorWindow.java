package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * A DatabaseCreatorWindow is a javaFX {@link Stage} that should be
 * used to display {@link DatabaseCreatorView} gui-objects.
 */
public class DatabaseCreatorWindow extends LibraryAppStage {

    private static final double WIDTH = 741;
    private static final double HEIGHT = 400;

    public DatabaseCreatorWindow(DatabaseCreatorView view, Window owner) {
        super("window.dbcreator.title", view);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(owner);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.centerOnScreen();
    }
}
