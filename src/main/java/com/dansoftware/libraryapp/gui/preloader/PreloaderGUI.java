package com.dansoftware.libraryapp.gui.preloader;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link PreloaderGUI} is used by {@link com.dansoftware.libraryapp.main.Preloader} for creating the
 * preloader gui easily.
 *
 * <p>
 * A {@link PreloaderGUI} can be constructed through a {@link PreloaderGUI.Builder} object.
 *
 * @author Daniel Gyorffy
 */
public class PreloaderGUI extends VBox {

    private static final String STYLE_CLASS = "root";
    private static final String LOGO_STYLE_CLASS = "centerLogo";
    private static final String COMPANY_LOGO_STYLE_CLASS = "companyLogo";
    private static final double PREF_WIDTH = 739.0;
    private static final double PREF_HEIGHT = 453.0;

    private final StackPane mainPane;
    private final ImageView center;

    private PreloaderGUI(@NotNull Builder builder) {
        this.center = buildCenterLogo();
        this.mainPane = buildMainPane(center, buildCompanyLogo());
        StringProperty messageProperty = builder.getStringProperty()
                .orElseGet(SimpleStringProperty::new);
        Label messageLabel = buildMessageLabel(messageProperty);
        this.buildUI(mainPane, messageLabel);
    }

    private Label buildMessageLabel(StringProperty textProperty) {
        Label label = new Label();
        label.textProperty().bind(textProperty);
//        label.setFont(Font.font("System", FontWeight.NORMAL, 20));
//        label.setTextFill(Color.BLACK);
        StackPane.setAlignment(label, Pos.BOTTOM_LEFT);
        StackPane.setMargin(label, new Insets(0, 0, 5, 10));
        return label;
    }

    private ImageView buildCenterLogo() {
        var center = new ImageView();
        center.getStyleClass().add(LOGO_STYLE_CLASS);
        center.setFitHeight(294.0);
        center.setFitWidth(301.0);
        center.setPickOnBounds(true);
        center.setPreserveRatio(true);
        return center;
    }

    private StackPane buildMainPane(@NotNull ImageView center,
                                    @NotNull ImageView companyLogo) {
        var mainPane = new StackPane();
        mainPane.setPrefHeight(448.0);
        mainPane.setPrefWidth(739.0);
        mainPane.getChildren().add(center);
        StackPane.setMargin(mainPane, new Insets(0, 0, 10.0, 0));
        mainPane.getChildren().add(companyLogo);
        return mainPane;
    }

    private ImageView buildCompanyLogo() {
        ImageView companyLogo = new ImageView();
        companyLogo.getStyleClass().add(COMPANY_LOGO_STYLE_CLASS);
        companyLogo.setFitHeight(84.0);
        companyLogo.setFitWidth(94.0);
        companyLogo.setPickOnBounds(true);
        companyLogo.setPreserveRatio(true);
        StackPane.setAlignment(companyLogo, Pos.TOP_LEFT);
        StackPane.setMargin(companyLogo, new Insets(5.0));
        return companyLogo;
    }

    private ProgressBar buildProgressBar() {
        ProgressBar progressBar = new com.jfoenix.controls.JFXProgressBar();
        progressBar.setPrefHeight(8.0);
        progressBar.setPrefWidth(747.0);
        return progressBar;
    }

    private void buildUI(@NotNull StackPane mainPane,
                         @NotNull Label messageLabel) {
        this.setPrefHeight(PREF_HEIGHT);
        this.setPrefWidth(PREF_WIDTH);
        this.getStyleClass().add(STYLE_CLASS);

        this.getChildren().add(mainPane);
        this.getChildren().add(buildProgressBar());
        mainPane.getChildren().add(messageLabel);
        this.getStylesheets().add("/com/dansoftware/libraryapp/gui/preloader/preloader.css");
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

        private StringProperty stringProperty;

        private Builder() {
        }

        private Optional<StringProperty> getStringProperty() {
            return Optional.ofNullable(stringProperty);
        }

        public Builder messageProperty(StringProperty stringProperty) {
            this.stringProperty = stringProperty;
            return this;
        }

        public PreloaderGUI build() {
            return new PreloaderGUI(this);
        }
    }
}
