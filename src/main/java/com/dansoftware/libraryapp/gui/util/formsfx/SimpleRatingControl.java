package com.dansoftware.libraryapp.gui.util.formsfx;

import com.dlsc.formsfx.model.structure.IntegerField;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.Rating;

public class SimpleRatingControl extends SimpleControl<IntegerField> {

    private static final String STYLE_CLASS = "simple-rating-control";

    private final int max;

    private Label fieldLabel;
    private StackPane ratingPane;
    private Rating rating;

    public SimpleRatingControl(int max) {
        this.max = max;
    }

    @Override
    public void initializeParts() {
        super.initializeParts();

        getStyleClass().add(STYLE_CLASS);

        fieldLabel = new Label(field.labelProperty().getValue());
        rating = new Rating(max, field.getValue());
        ratingPane = new StackPane();
        rating.setRating(field.getValue());
    }

    @Override
    public void layoutParts() {
        super.layoutParts();

        StackPane.setAlignment(rating, Pos.CENTER_LEFT);
        ratingPane.getChildren().add(rating);

        add(fieldLabel, 0, 0, 2, 1);
        add(ratingPane, 2, 0, 2, 1);
    }

    @Override
    public void setupBindings() {
        super.setupBindings();

        rating.disableProperty().bind(field.editableProperty().not());
        fieldLabel.textProperty().bind(field.labelProperty());
    }

    @Override
    public void setupValueChangedListeners() {
        super.setupValueChangedListeners();

        field.errorMessagesProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(rating));
        field.tooltipProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(rating));

        rating.focusedProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(rating));
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> field.valueProperty().set(newValue.intValue()));
        field.valueProperty().addListener((observable, oldValue, newValue) -> rating.ratingProperty().set(newValue.intValue()));
    }

    @Override
    public void setupEventHandlers() {
        setOnMouseEntered(event -> toggleTooltip(rating));
        setOnMouseExited(event -> toggleTooltip(rating));
    }
}
