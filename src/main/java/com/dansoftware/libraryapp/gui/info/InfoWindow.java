package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.util.Bundles;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.dansoftware.libraryapp.main.Main.getPrimaryStage;

/**
 * An InfoWindow can show all the information
 * of the application in it's scene.
 */
public class InfoWindow extends Stage {

    /**
     * Creates the InfoWindow and loads the information-view
     * into it's scene + sets some property of the window.
     *
     * @throws IOException if the FXML file couldn't be read
     */
    public InfoWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("info-view.fxml"), Bundles.getCommonBundle());

        Parent root = loader.load();
        Scene scene = new Scene(root);

        this.setScene(scene);
        this.sizeToScene();
        this.setTitle("LibraryApp Info");
        this.initStyle(StageStyle.UNDECORATED);
        this.getIcons().add(Globals.WINDOW_ICON);

        getPrimaryStage().ifPresent(this::initOwner);
    }

}
