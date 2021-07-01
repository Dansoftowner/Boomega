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

package com.dansoftware.boomega.gui.firsttime;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.api.*;
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
public class FirstTimeDialog extends SegmentDialog implements EmptyContext {

    private static final String STYLE_CLASS = "firstTimeDialog";

    public FirstTimeDialog(@NotNull Preferences preferences) {
        super(I18N.getValues(), new DialogSegmentSequence(preferences));
        setCustomButtons(Collections.singletonList(new SkipButton()));
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getStyleClass().add(STYLE_CLASS);
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
