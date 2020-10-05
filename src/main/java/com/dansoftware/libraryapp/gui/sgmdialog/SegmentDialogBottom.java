package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.ResourceBundle;

public class SegmentDialogBottom extends BorderPane
        implements ChangeListener<Segment> {

    private static final Logger logger = LoggerFactory.getLogger(SegmentDialogBottom.class);

    private static final String NEXT_BUTTON_STRING = "segment.dialog.button.next";
    private static final String FINISH_BUTTON_STRING = "segment.dialog.button.finish";
    private static final String PREV_BUTTON_STRING = "segment.dialog.button.prev";

    private final SegmentSequence segmentSequence;

    private final BiStringProperty nextItemTextProperty;
    private final BiStringProperty prevItemTextProperty;

    private final Button nextItemButton;
    private final Button prevItemButton;

    private final HBox rightBox;

    private final ResourceBundle resourceBundle;

    public SegmentDialogBottom(@NotNull ResourceBundle resourceBundle,
                               @Nullable Button customButton,
                               @NotNull SegmentSequence segmentSequence) {
        this.resourceBundle = Objects.requireNonNull(resourceBundle, "ResourceBundle is needed!");
        this.segmentSequence = Objects.requireNonNull(segmentSequence, "segmentSequence shouldn't be null");
        this.nextItemButton = new Button();
        this.prevItemButton = new Button();
        this.rightBox = new HBox(10, nextItemButton);

        this.nextItemTextProperty = new BiStringProperty(resourceBundle.getString(NEXT_BUTTON_STRING), StringUtils.EMPTY, StringUtils.EMPTY);
        this.prevItemTextProperty = new BiStringProperty(resourceBundle.getString(PREV_BUTTON_STRING), StringUtils.EMPTY, StringUtils.EMPTY);

        this.nextItemButton.textProperty().bind(nextItemTextProperty);
        this.prevItemButton.textProperty().bind(prevItemTextProperty);

        nextItemButton.setOnAction(event -> segmentSequence.navigateNext());
        prevItemButton.setOnAction(event -> segmentSequence.navigateBack());

        this.setLeft(customButton);
        this.setRight(rightBox);

        segmentSequence.focusedSegmentProperty().addListener(this);
        this.init(segmentSequence);
    }

    public SegmentDialogBottom(@NotNull ResourceBundle resourceBundle,
                               @NotNull SegmentSequence segmentSequence) {
        this(resourceBundle, null, segmentSequence);
    }

    private void init(SegmentSequence segmentSequence) {
        this.changed(segmentSequence.focusedSegmentProperty(), null, segmentSequence.getFocusedSegment());
    }

    @Override
    public void changed(ObservableValue<? extends Segment> observable,
                        Segment oldValue,
                        Segment newValue) {
        if (newValue != null) {

            if (segmentSequence.isSegmentLast(newValue)) {
                nextItemTextProperty.setFixValue(resourceBundle.getString(FINISH_BUTTON_STRING));
            } else {
                Segment nextSegment = segmentSequence.getNextFrom(newValue);
                nextItemTextProperty.set(resourceBundle.getString(NEXT_BUTTON_STRING), nextSegment.getTitle());
            }

            if (segmentSequence.isSegmentFirst(newValue)) {
                rightBox.getChildren().remove(prevItemButton);
            } else {
                try {
                    rightBox.getChildren().add(0, prevItemButton);
                } catch (IllegalArgumentException ignored) {
                    //we don't care, if the previous button is already on the scene
                }

                Segment previousSegment = segmentSequence.getPrevFrom(newValue);
                prevItemTextProperty.set(resourceBundle.getString(PREV_BUTTON_STRING), previousSegment.getTitle());
            }
        }
    }

    private static final class BiStringProperty extends SimpleStringProperty {

        BiStringProperty(String prefix,
                         String suffix,
                         String separator) {
            this.set(prefix, suffix, separator);
        }

        void setFixValue(String value) {
            set(value, StringUtils.EMPTY, StringUtils.EMPTY);
        }

        void set(String prefix, String suffix) {
            set(prefix, suffix, ": ");
        }

        void set(String prefix, String suffix, String separator) {
            set(getFinalValue(prefix, suffix, separator));
        }

        private String getFinalValue(String prefix, String suffix, String separator) {
            return prefix == null ? suffix : prefix + separator + suffix;
        }
    }
}
