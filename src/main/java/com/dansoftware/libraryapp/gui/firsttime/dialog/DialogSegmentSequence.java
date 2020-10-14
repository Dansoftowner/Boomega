package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.gui.firsttime.dialog.segment.theme.ThemeSegment;
import com.dansoftware.sgmdialog.SegmentDialog;
import com.dansoftware.sgmdialog.SegmentSequence;
import org.jetbrains.annotations.NotNull;

public class DialogSegmentSequence extends SegmentSequence {

    DialogSegmentSequence() {
        super(new ThemeSegment());
    }

    @Override
    protected void onSegmentsFinished(@NotNull SegmentDialog segmentDialog) {

    }
}
