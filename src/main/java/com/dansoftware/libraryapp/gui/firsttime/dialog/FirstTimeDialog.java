package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.sgmdialog.SegmentDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * The {@link FirstTimeDialog} is used for configuring the app at the first run.
 *
 * @author Daniel Gyorffy
 */
public class FirstTimeDialog extends SegmentDialog implements ContextTransformable {

    private static final String STYLE_CLASS = "firstTimeDialog";

    public FirstTimeDialog(@NotNull Preferences preferences) {
        super(I18N.getFirstTimeDialogValues(), new DialogSegmentSequence(preferences));
        setCustomButtons(Collections.singletonList(new SkipButton()));
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getStyleClass().add(STYLE_CLASS);
    }

    @Override
    public @NotNull Context getContext() {
        return Context.empty();
    }

    private class SkipButton extends Button implements EventHandler<ActionEvent> {
        SkipButton() {
            super(I18N.getFirstTimeDialogValues().getString("segment.dialog.button.skip"));
            setOnAction(this);
        }

        @Override
        public void handle(ActionEvent event) {
            FirstTimeDialog.this.getSegmentSequence().skipAll();
        }
    }
}
