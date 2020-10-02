package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.ResourceBundle;

public class SegmentDialog extends BorderPane {

    private final SegmentSequence segmentSequence;
    private final SegmentLabelSequence labelSequence;
    private final SegmentDialogBottom dialogBottom;

    public SegmentDialog(@NotNull ResourceBundle resourceBundle,
                         @NotNull SegmentSequence segmentSequence,
                         @Nullable Button customButton) {
        this.segmentSequence = Objects.requireNonNull(segmentSequence, "segmentSequence shouldn't be null");
        this.labelSequence = new SegmentLabelSequence(segmentSequence);
        this.dialogBottom = new SegmentDialogBottom(resourceBundle, customButton, segmentSequence);
        this.setPadding(new Insets(10));
        this.setTop(labelSequence);
        this.setBottom(dialogBottom);
    }

    public SegmentDialog(@NotNull ResourceBundle resourceBundle,
                         @NotNull SegmentSequence segmentSequence) {
        this(resourceBundle, segmentSequence, null);
    }

}
