package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SegmentLabelSequence extends HBox
        implements ChangeListener<Segment> {

    private final Map<Segment, LabelImpl> segmentLabelMap;
    private final SegmentSequence segmentSequence;

    public SegmentLabelSequence(@NotNull SegmentSequence segmentSequence) {
        this.segmentSequence = Objects.requireNonNull(segmentSequence, "segmentSequence shouldn't be null");
        this.segmentLabelMap = new HashMap<>();
        this.createGui(segmentSequence);
        this.setSpacing(10);
        this.segmentSequence.focusedSegmentProperty().addListener(this);
    }

    private void createGui(SegmentSequence segmentSequence) {
        for (Iterator<Segment> iterator = segmentSequence.iterator(); iterator.hasNext(); ) {
            Segment segment = iterator.next();
            LabelImpl label = new LabelImpl(segment.getTitle());
            segmentLabelMap.put(segment, label);
            this.getChildren().add(label);
            if (iterator.hasNext())
                this.getChildren().add(new Arrow());
        }
    }

    @Override
    public void changed(ObservableValue<? extends Segment> observable,
                        Segment oldValue,
                        Segment newValue) {
        segmentLabelMap.get(oldValue).setFocusedState(false);
        segmentLabelMap.get(newValue).setFocusedState(true);
    }

    private static final class Arrow extends SVGPath {

        private static final String CLASS_NAME = "segmentLabelArrow";

        Arrow() {
            getStyleClass().add(CLASS_NAME);
            setContent("M18.629 15.997l-7.083-7.081L13.462 7l8.997 8.997L13.457 25l-1.916-1.916z");
        }
    }

    private static final class LabelImpl extends Label {

        private static final String CLASS_NAME = "segmentLabel";

        LabelImpl(String text) {
            super(text);
            getStyleClass().add(CLASS_NAME);
        }

        public void setFocusedState(boolean value) {
            pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), value);
        }
    }
}
