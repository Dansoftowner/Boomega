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
import com.dansoftware.boomega.gui.firsttime.segment.lang.LanguageSegment;
import com.dansoftware.boomega.gui.firsttime.segment.theme.ThemeSegment;
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
