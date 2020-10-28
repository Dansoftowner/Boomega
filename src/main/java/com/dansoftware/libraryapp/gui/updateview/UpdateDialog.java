package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.updateview.segment.notification.NotificationSegment;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.SegmentDialog;
import com.dansoftware.sgmdialog.SegmentSequence;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class UpdateDialog extends SegmentDialog {

    private final Context context;
    private final UpdateInformation information;

    public UpdateDialog(@NotNull Context context, @NotNull UpdateInformation information) {
        super(I18N.getUpdateDialogValues(), new SegmentSequenceImpl(context, information));
        this.context = context;
        this.information = information;
        setCustomButtons(Collections.singletonList(new SkipButton()));
    }

    private static final class SkipButton extends Button implements EventHandler<ActionEvent> {
        SkipButton() {
            super(I18N.getUpdateDialogValues().getString("segment.dialog.button.skip"));
            this.setOnAction(this);
        }

        @Override
        public void handle(ActionEvent event) {

        }
    }

    private static final class SegmentSequenceImpl extends SegmentSequence {
        SegmentSequenceImpl(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
            super(new NotificationSegment(context, updateInformation));
        }

        @Override
        protected void onSegmentsFinished(@NotNull SegmentDialog segmentDialog) {
            WindowUtils.getStageOptionalOf(segmentDialog).ifPresent(Stage::close);
        }
    }
}
