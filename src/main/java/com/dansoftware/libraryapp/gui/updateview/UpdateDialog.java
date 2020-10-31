package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.updateview.segment.detail.DetailsSegment;
import com.dansoftware.libraryapp.gui.updateview.segment.download.DownloadSegment;
import com.dansoftware.libraryapp.gui.updateview.segment.notification.NotificationSegment;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.Segment;
import com.dansoftware.sgmdialog.SegmentDialog;
import com.dansoftware.sgmdialog.SegmentSequence;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class UpdateDialog extends SegmentDialog {

    private static final String STYLE_CLASS = "updateDialog";

    private final Context context;
    private final UpdateInformation information;

    public UpdateDialog(@NotNull Context context, @NotNull UpdateInformation information) {
        super(I18N.getUpdateDialogValues(), new SegmentSequenceImpl(context, information));
        this.context = context;
        this.information = information;
        this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.getStyleClass().add(STYLE_CLASS);
        this.setCustomButtons(Collections.singletonList(new LaterButton()));
        getSegmentSequence().focusedSegmentProperty().addListener(new LaterButtonHiderAction());
    }

    private final class LaterButtonHiderAction implements ChangeListener<Segment> {
        @Override
        public void changed(ObservableValue<? extends Segment> observable, Segment oldValue, Segment newValue) {
            List<Button> customButtons = getSegmentSequence().isSegmentLast(newValue) ? Collections.emptyList() :
                    Collections.singletonList(new LaterButton());
            setCustomButtons(customButtons);
        }
    }

    private static final class LaterButton extends Button implements EventHandler<ActionEvent> {
        LaterButton() {
            super(I18N.getUpdateDialogValues().getString("segment.dialog.button.later"));
            setOnAction(this);
            HBox.setMargin(this, new Insets(0, 10, 0, 0));
        }

        @Override
        public void handle(ActionEvent event) {

        }
    }

    /*private final class SkipButton extends Button implements EventHandler<ActionEvent> {
        SkipButton() {
            super(I18N.getUpdateDialogValues().getString("segment.dialog.button.skip"));
            this.setOnAction(this);
        }

        @Override
        public void handle(ActionEvent event) {
            SegmentSequence segmentSequence = getSegmentSequence();
            Segment lastSegment = segmentSequence.getFocusedSegment();
            while (segmentSequence.getNextFrom(lastSegment) != null)
                lastSegment = segmentSequence.getNextFrom(lastSegment);
            segmentSequence.focusedSegmentProperty().set(lastSegment);
        }
    }*/

    private static final class SegmentSequenceImpl extends SegmentSequence {
        SegmentSequenceImpl(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
            super(
                    new NotificationSegment(context, updateInformation),
                    new DetailsSegment(context, updateInformation),
                    new DownloadSegment(context, updateInformation)
            );
        }

        @Override
        protected void onSegmentsFinished(@NotNull SegmentDialog segmentDialog) {
//            WindowUtils.getStageOptionalOf(segmentDialog).ifPresent(Stage::close);
        }
    }
}
