/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.control.formsfx;

import com.dlsc.formsfx.model.structure.IntegerField;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.Rating;

public class SimpleRatingControl extends SimpleControl<IntegerField> {

    private static final String STYLE_CLASS = "simple-rating-control";

    private final IntegerProperty origin;
    private final int max;

    private Label fieldLabel;
    private StackPane ratingPane;
    private Rating rating;

    public SimpleRatingControl(int max, IntegerProperty origin) {
        this.max = max;
        this.origin = origin;
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
        field.valueProperty().bindBidirectional(rating.ratingProperty());

        rating.focusedProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(rating));
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> origin.set(newValue.intValue()));
    }

    @Override
    public void setupEventHandlers() {
        setOnMouseEntered(event -> toggleTooltip(rating));
        setOnMouseExited(event -> toggleTooltip(rating));
    }
}
