package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.main.Globals;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * An InfoWindow can show all the information
 * of the application in it's scene.
 */
public class InfoWindow extends Stage {

    /**
     * Creates the InfoWindow and sets the information-view
     * into it's scene + sets some property of the window.
     */
    public InfoWindow(InfoView view) {


        Scene scene = new Scene(view);

        this.setScene(scene);
        this.sizeToScene();
        this.setTitle("LibraryApp Info");
        this.initStyle(StageStyle.UNDECORATED);
        this.getIcons().add(Globals.WINDOW_ICON);
    }

}
