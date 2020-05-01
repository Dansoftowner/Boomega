package com.dansoftware.libraryapp.gui.preloader;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PreloaderGUI extends VBox {

    private ImageView center;

    public PreloaderGUI() {
        this.setPrefHeight(453.0);
        this.setPrefWidth(739.0);
        this.getStyleClass().add("root");

        StackPane mainPane = new StackPane();
        mainPane.setPrefHeight(448.0);
        mainPane.setPrefWidth(739.0);

        center = new ImageView(
                new Image(getClass().getResourceAsStream("/com/dansoftware/libraryapp/image/logo512px_black.png"))
        );

        center.setFitHeight(294.0);
        center.setFitWidth(301.0);
        center.setPickOnBounds(true);
        center.setPreserveRatio(true);

        mainPane.getChildren().add(center);

        StackPane.setMargin(mainPane, new Insets(0, 0, 10.0, 0));

        ImageView companyLogo = new ImageView(new Image(
                getClass().getResourceAsStream("/com/dansoftware/libraryapp/image/dansoftware-logo.jpg")
        ));
        companyLogo.setFitHeight(84.0);
        companyLogo.setFitWidth(94.0);
        companyLogo.setPickOnBounds(true);
        companyLogo.setPreserveRatio(true);

        mainPane.getChildren().add(companyLogo);

        StackPane.setAlignment(companyLogo, Pos.TOP_LEFT);
        StackPane.setMargin(companyLogo, new Insets(5.0));

        this.getChildren().add(mainPane);

        ProgressBar progressBar = new com.jfoenix.controls.JFXProgressBar();
        progressBar.setPrefHeight(8.0);
        progressBar.setPrefWidth(747.0);

        this.getChildren().add(progressBar);

        this.getStylesheets().add("/com/dansoftware/libraryapp/gui/preloader/preloader.css");
    }

    public ImageView getCenter() {
        return center;
    }
}
