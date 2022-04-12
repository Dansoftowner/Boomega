/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.firsttime.segment.theme;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.i18n.api.I18N;
import com.dansoftware.sgmdialog.FixedContentTitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible {@link com.dansoftware.sgmdialog.Segment} for choosing the app-theme
 *
 * @author Daniel Gyorffy
 */
public class ThemeSegment extends FixedContentTitledSegment {

    private final Preferences preferences;

    public ThemeSegment(@NotNull Preferences preferences) {
        super(I18N.getValues().getString("segment.theme.name"), I18N.getValues().getString("segment.theme.title"));
        this.preferences = preferences;
    }

    @Override
    protected @NotNull Node createCenterContent() {
        return new ThemeSegmentView(preferences);
    }
}
