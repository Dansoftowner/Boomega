/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.updatedialog;

import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.updatedialog.segment.detail.DetailsSegment;
import com.dansoftware.boomega.gui.updatedialog.segment.download.DownloadSegment;
import com.dansoftware.boomega.gui.updatedialog.segment.notification.NotificationSegment;
import com.dansoftware.boomega.update.Release;
import com.dansoftware.boomega.util.InMemoryResourceBundle;
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
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

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
    private final HidePolicy hidePolicy;

    public UpdateDialog(@NotNull Context context, @NotNull Release githubRelease, @NotNull HidePolicy hidePolicy) {
        super(buildSegmentDialogValues(), new SegmentSequenceImpl(context, githubRelease));
        this.context = context;
        this.hidePolicy = hidePolicy;
        this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.getStyleClass().add(STYLE_CLASS);
        this.setCustomButtons(Collections.singletonList(new LaterButton()));
        getSegmentSequence().focusedSegmentProperty().addListener(new LaterButtonHiderAction());
    }

    private static ResourceBundle buildSegmentDialogValues() {
        return new InMemoryResourceBundle.Builder()
                .put("segment.dialog.button.next", i18n("update.dialog.button.next"))
                .put("segment.dialog.button.finish", i18n("update.dialog.button.finish"))
                .put("segment.dialog.button.prev", i18n("update.dialog.button.prev"))
                .put("segment.dialog.button.skip", i18n("update.dialog.button.skip"))
                .build();
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
            super(i18n("update.dialog.button.later"));
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

        SegmentSequenceImpl(@NotNull Context context, @NotNull Release githubRelease) {
            super(
                    new NotificationSegment(context, githubRelease),
                    new DetailsSegment(context, githubRelease),
                    new DownloadSegment(context, githubRelease)
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
