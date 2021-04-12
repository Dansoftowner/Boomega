package com.dansoftware.boomega.gui.firsttime.dialog;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.firsttime.dialog.segment.lang.LanguageSegment;
import com.dansoftware.boomega.gui.firsttime.dialog.segment.theme.ThemeSegment;
import com.dansoftware.boomega.gui.util.WindowUtils;
import com.dansoftware.sgmdialog.SegmentDialog;
import com.dansoftware.sgmdialog.SegmentSequence;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SegmentSequence} for the {@link FirstTimeDialog}.
 *
 * @author Daniel Gyorffy
 */
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
