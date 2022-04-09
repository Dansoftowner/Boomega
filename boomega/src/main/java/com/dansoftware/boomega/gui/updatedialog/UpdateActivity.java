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

package com.dansoftware.boomega.gui.updatedialog;

import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.update.Release;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for showing update-downloader dialog
 *
 * @author Daniel Gyorffy
 */
public class UpdateActivity {

    private static final Logger logger = LoggerFactory.getLogger(UpdateActivity.class);

    private final Context context;
    private final Release updateSearchResult;

    /**
     * Creates a basic {@link UpdateActivity}.
     *
     * @param context            for displaying the dialogs.
     * @param updateSearchResult the found github release
     */
    public UpdateActivity(@NotNull Context context,
                          @NotNull Release updateSearchResult) {
        this.context = context;
        this.updateSearchResult = updateSearchResult;
    }

    /**
     * Shows the activity through the particular {@link Context}.
     */
    public void show() {
        UpdateDialog updateDialog = new UpdateDialog(context, updateSearchResult,
                (context, updDialog) -> context.hideOverlay((Region) updDialog.getParent().getParent()));
        this.context.showOverlay(new StackPane(new Group(updateDialog)), true);
    }
}
