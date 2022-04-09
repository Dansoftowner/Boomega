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

package com.dansoftware.boomega.gui.updatedialog.segment.detail;

import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.i18n.api.I18N;
import com.dansoftware.boomega.update.Release;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Downloads the preview markdown-text about the new update and also renders it, so the user can
 * explore the new features and/or bug fixes in a {@link com.dansoftware.boomega.gui.updatedialog.UpdateDialog}.
 *
 * @author Daniel Gyorffy
 */
public class DetailsSegment extends FixedContentSegment {

    private final Context context;
    private final Release githubRelease;

    public DetailsSegment(@NotNull Context context, @NotNull Release release) {
        super(I18N.getValues().getString("update.segment.details.name"));
        this.context = context;
        this.githubRelease = release;
    }

    @Override
    protected @NotNull Node createContent() {
        return new DetailsSegmentView(githubRelease);
    }
}
