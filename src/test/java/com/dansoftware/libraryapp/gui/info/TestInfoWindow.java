package com.dansoftware.libraryapp.gui.info;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

public class TestInfoWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        InfoWindow infoWindow = new InfoWindow(new InfoView());
        infoWindow.show();
    }
}
