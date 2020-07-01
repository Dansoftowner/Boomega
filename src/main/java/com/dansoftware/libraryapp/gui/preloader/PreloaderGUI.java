package com.dansoftware.libraryapp.gui.preloader;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

public class PreloaderGUI extends VBox {

    private StackPane mainPane;
    private ImageView center;

    private PreloaderGUI() {
        this.setPrefHeight(453.0);
        this.setPrefWidth(739.0);
        this.getStyleClass().add("root");

        mainPane = new StackPane();
        mainPane.setPrefHeight(448.0);
        mainPane.setPrefWidth(739.0);

        this.center = new ImageView(
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

    private PreloaderGUI(File file) {
        this();

        Label label = new Label(
                MessageFormat.format(getGeneralWord("preloader.gui.opening"), file.getName())
        );
        label.setFont(Font.font("System", FontWeight.NORMAL, 20));
        label.setTextFill(Color.BLACK);

        this.mainPane.getChildren().add(label);
        StackPane.setAlignment(label, Pos.BOTTOM_LEFT);
        StackPane.setMargin(label, new Insets(0, 0, 5, 10));
    }

    public void logoAnimation() {
        new animatefx.animation
                .BounceIn(this.center)
                .play();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<String> parameters;

        private Builder() {
        }

        public Builder parameters(List<String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public PreloaderGUI build() {
            if (CollectionUtils.isNotEmpty(this.parameters)) {
                return new PreloaderGUI(new File(this.parameters.get(0)));
            }

            return new PreloaderGUI();
        }
    }
}
