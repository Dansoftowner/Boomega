package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.locale.Bundles;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * An InfoWindow can show all the information
 * of the application in it's scene.
 */
public class InfoWindow extends Stage {

    public InfoWindow(AppEntry appEntry) throws IOException {
        this();

        if (appEntry != null) {
        }
        //this.initOwner(entryPoint.getPrimaryStage());
    }

    /**
     * Creates the InfoWindow and loads the information-view
     * into it's scene + sets some property of the window.
     */
    public InfoWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("info-view.fxml"), Bundles.getFXMLValues());

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);

        this.setScene(scene);
        this.sizeToScene();
        this.setTitle("LibraryApp Info");
        this.initStyle(StageStyle.UNDECORATED);
        this.getIcons().add(Globals.WINDOW_ICON);
    }

}
