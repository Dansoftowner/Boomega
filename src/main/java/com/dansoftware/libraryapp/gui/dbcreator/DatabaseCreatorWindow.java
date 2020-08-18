package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.main.Globals;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import static com.dansoftware.libraryapp.locale.I18N.getFXMLValues;

/**
 * A DatabaseCreatorWindow is a javaFX {@link Stage} that should be
 * used to display {@link DatabaseCreatorView} gui-objects.
 */
public class DatabaseCreatorWindow extends Stage {

    private static final double WIDTH = 741;
    private static final double HEIGHT = 400;

    public DatabaseCreatorWindow(DatabaseCreatorView view, Window owner) {
        this.setTitle(getFXMLValues().getString("data.source.adder.window.title"));
        this.setScene(new Scene(view));
        this.initModality(Modality.APPLICATION_MODAL);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.centerOnScreen();
        this.getIcons().addAll(Globals.windowIconPack());
        Theme.applyDefault((Themeable) view);
    }
}
