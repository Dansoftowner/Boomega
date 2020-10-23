package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.firsttime.dialog.segment.lang.LanguageSegment;
import com.dansoftware.libraryapp.gui.firsttime.dialog.segment.theme.ThemeSegment;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.sgmdialog.SegmentDialog;
import com.dansoftware.sgmdialog.SegmentSequence;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DialogSegmentSequence extends SegmentSequence {

    private static final Logger logger = LoggerFactory.getLogger(DialogSegmentSequence.class);

    DialogSegmentSequence(@NotNull Preferences preferences) {
        super(new ThemeSegment(preferences), new LanguageSegment(preferences));
    }

    @Override
    protected void onSegmentsFinished(@NotNull SegmentDialog segmentDialog) {
        WindowUtils.getStageOptionalOf(segmentDialog).ifPresent(Stage::close);
    }
}
