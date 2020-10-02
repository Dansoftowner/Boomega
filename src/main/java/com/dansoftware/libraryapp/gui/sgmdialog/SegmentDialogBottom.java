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

import java.util.Objects;
import java.util.ResourceBundle;

public class SegmentDialogBottom extends BorderPane
        implements ChangeListener<Segment> {

    private static final String NEXT_BUTTON_STRING = "segment.dialog.button.next";
    private static final String FINISH_BUTTON_STRING = "segment.dialog.button.finish";
    private static final String PREV_BUTTON_STRING = "segment.dialog.button.prev";

    private final SegmentSequence segmentSequence;

    private final BiStringProperty nextItemTextProperty;
    private final BiStringProperty prevItemTextProperty;

    private final Button nextItemButton;
    private final Button prevItemButton;
    private Button customButton;

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
        this.customButton = customButton;

        this.nextItemTextProperty = new BiStringProperty(resourceBundle.getString(NEXT_BUTTON_STRING), StringUtils.EMPTY);
        this.prevItemTextProperty = new BiStringProperty(resourceBundle.getString(PREV_BUTTON_STRING), StringUtils.EMPTY);

        this.nextItemButton.textProperty().bind(nextItemTextProperty);
        this.prevItemButton.textProperty().bind(prevItemTextProperty);

        nextItemButton.setOnAction(e -> segmentSequence.navigateNextFrom(segmentSequence.getFocusedSegment()));
        prevItemButton.setOnAction(e -> segmentSequence.navigateBackFrom(segmentSequence.getFocusedSegment()));

        this.setLeft(customButton);
        this.setRight(rightBox);

        segmentSequence.focusedSegmentProperty().addListener(this);
    }

    public SegmentDialogBottom(@NotNull ResourceBundle resourceBundle,
                               @NotNull SegmentSequence segmentSequence) {
        this(resourceBundle, null, segmentSequence);
    }

    @Override
    public void changed(ObservableValue<? extends Segment> observable,
                        Segment oldValue,
                        Segment newValue) {
        if (newValue != null) {

            if (segmentSequence.isSegmentFirst(newValue)) {
                rightBox.getChildren().remove(prevItemButton);
            } else {
                rightBox.getChildren().add(0, prevItemButton);

                if (segmentSequence.isSegmentLast(newValue)) nextItemTextProperty.set(
                        resourceBundle.getString(FINISH_BUTTON_STRING), null);
                else prevItemTextProperty.set(
                        resourceBundle.getString(PREV_BUTTON_STRING),
                        segmentSequence.getPrevFrom(newValue).getTitle()
                );
            }
        }
    }

    private static final class BiStringProperty extends SimpleStringProperty {
        private String prefix;
        private String suffix;

        BiStringProperty(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.set(getFinalValue());
        }

        void setPrefix(String prefix) {
            this.prefix = prefix;
            this.set(getFinalValue());
        }

        void setSuffix(String suffix) {
            this.suffix = suffix;
            this.set(getFinalValue());
        }

        void set(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            set(getFinalValue());
        }

        private String getFinalValue() {
            return prefix == null ? suffix : prefix + ':' + suffix;
        }
    }
}
