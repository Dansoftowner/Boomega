package com.dansoftware.boomega.gui.preloader;

import com.dansoftware.boomega.i18n.I18N;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link PreloaderGUI} is used by {@link com.dansoftware.boomega.main.Preloader} for creating the
 * preloader gui easily.
 *
 * <p>
 * A {@link PreloaderGUI} can be constructed through a {@link PreloaderGUI.Builder} object.
 *
 * @author Daniel Gyorffy
 */
public class PreloaderGUI extends VBox {

    private static final String MAIN_PANE_STYLE_CLASS = "mainPane";
    private static final String LOGO_STYLE_CLASS = "centerLogo";
    private static final String COMPANY_LABEL_STYLE_CLASS = "companyLabel";
    private static final String LABEL_STYLE_CLASS = "messageLabel";
    private static final String APP_NAME_LABEL_STYLE_CLASS = "app-name-label";

    private final StackPane mainPane;
    private final Node center;

    private PreloaderGUI(@NotNull Builder builder) {
        this.center = buildCenter();
        this.mainPane = buildMainPane(center, buildCompanyLabel());
        StringProperty messageProperty = builder.getStringProperty()
                .orElseGet(SimpleStringProperty::new);
        Label messageLabel = buildMessageLabel(messageProperty);
        this.buildUI(mainPane, messageLabel);
    }

    private Label buildMessageLabel(StringProperty textProperty) {
        Label label = new Label();
        label.getStyleClass().add(LABEL_STYLE_CLASS);
        label.textProperty().bind(textProperty);
        label.prefWidthProperty().bind(mainPane.widthProperty());
        label.visibleProperty().bind(textProperty.isNotEmpty());
        StackPane.setAlignment(label, Pos.BOTTOM_LEFT);
        StackPane.setMargin(label, new Insets(0, 0, 15, 10));
        return label;
    }

    private Node buildCenter() {
        return new Group(
                new VBox(0,
                        new StackPane(buildCenterLogo()),
                        new StackPane(buildAppNameLabel())
                )
        );
    }

    private Label buildAppNameLabel() {
        var label = new Label(I18N.getValue("app.name"));
        label.getStyleClass().add(APP_NAME_LABEL_STYLE_CLASS);
        return label;
    }

    private ImageView buildCenterLogo() {
        var center = new ImageView();
        center.getStyleClass().add(LOGO_STYLE_CLASS);
        center.setPickOnBounds(true);
        center.setPreserveRatio(true);
        return center;
    }

    private StackPane buildMainPane(@NotNull Node center,
                                    @NotNull Label companyLabel) {
        var mainPane = new StackPane();
        mainPane.getStyleClass().add(MAIN_PANE_STYLE_CLASS);
        mainPane.getChildren().add(center);
        mainPane.getChildren().add(companyLabel);
        return mainPane;
    }

    private Label buildCompanyLabel() {
        Label label = new Label("Dansoftware");
        label.getStyleClass().add(COMPANY_LABEL_STYLE_CLASS);
        StackPane.setAlignment(label, Pos.TOP_RIGHT);
        return label;
    }

    private ProgressBar buildProgressBar() {
        ProgressBar progressBar = new com.jfoenix.controls.JFXProgressBar();
        progressBar.setProgress(Timeline.INDEFINITE);
        progressBar.prefWidthProperty().bind(mainPane.widthProperty());
        StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        return progressBar;
    }

    private void buildUI(@NotNull StackPane mainPane,
                         @NotNull Label messageLabel) {
        this.getChildren().add(mainPane);
        mainPane.getChildren().add(buildProgressBar());
        mainPane.getChildren().add(messageLabel);
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
