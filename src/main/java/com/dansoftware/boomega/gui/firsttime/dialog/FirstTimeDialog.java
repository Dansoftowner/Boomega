package com.dansoftware.boomega.gui.firsttime.dialog;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.i18n.I18N;
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
        super(I18N.getValues(), new DialogSegmentSequence(preferences));
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
            super(I18N.getValues().getString("segment.dialog.button.skip"));
            setOnAction(this);
        }

        @Override
        public void handle(ActionEvent event) {
            FirstTimeDialog.this.getSegmentSequence().skipAll();
        }
    }
}
