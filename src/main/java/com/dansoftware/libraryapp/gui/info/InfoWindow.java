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

public class InfoWindow extends Stage {

    public InfoWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("info-view.fxml"), Bundles.getCommonBundle());

        Parent root = loader.load();
        Scene scene = new Scene(root);

        this.setScene(scene);
        this.sizeToScene();
        this.setTitle("LibraryApp Info");
        //this.initOwner(getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.getIcons().add(Globals.WINDOW_ICON);
    }

}
