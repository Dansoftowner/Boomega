package com.dansoftware.libraryapp.gui.updatedialog;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.updatedialog.segment.detail.DetailsSegment;
import com.dansoftware.libraryapp.gui.updatedialog.segment.download.DownloadSegment;
import com.dansoftware.libraryapp.gui.updatedialog.segment.notification.NotificationSegment;
import com.dansoftware.libraryapp.i18n.I18N;
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
import java.util.function.BiConsumer;

/**
 * An {@link UpdateDialog} allows a user to review and download the new update.
 *
 * @author Daniel Gyorffy
 */
public class UpdateDialog extends SegmentDialog {

    private static final String STYLE_CLASS = "updateDialog";

    public interface HidePolicy extends BiConsumer<Context, UpdateDialog> {
    }

    private final Context context;
    private final UpdateInformation information;
    private final HidePolicy hidePolicy;

    public UpdateDialog(@NotNull Context context, @NotNull UpdateInformation information, @NotNull HidePolicy hidePolicy) {
        super(I18N.getUpdateDialogValues(), new SegmentSequenceImpl(context, information));
        this.context = context;
        this.information = information;
        this.hidePolicy = hidePolicy;
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

    private final class LaterButton extends Button implements EventHandler<ActionEvent> {
        LaterButton() {
            super(I18N.getUpdateDialogValues().getString("segment.dialog.button.later"));
            setOnAction(this);
            HBox.setMargin(this, new Insets(0, 10, 0, 0));
        }

        @Override
        public void handle(ActionEvent event) {
            UpdateDialog.this.hidePolicy.accept(context, UpdateDialog.this);
        }
    }

    private static final class SegmentSequenceImpl extends SegmentSequence {

        private final Context context;

        SegmentSequenceImpl(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
            super(
                    new NotificationSegment(context, updateInformation),
                    new DetailsSegment(context, updateInformation),
                    new DownloadSegment(context, updateInformation)
            );
            this.context = context;
        }

        @Override
        protected void onSegmentsFinished(@NotNull SegmentDialog segmentDialog) {
            UpdateDialog updateDialog = (UpdateDialog) segmentDialog;
            updateDialog.hidePolicy.accept(context, updateDialog);
        }
    }
}
