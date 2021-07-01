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
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.UpdateSearcher;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

/**
 * An UpdateActivity can show a user an update-downloader dialog, or
 * it can show an alert-dialog if the update-searching failed with an
 * exception.
 *
 * <p>
 * It needs a {@link Context} for displaying the dialog(s) and
 * an {@link UpdateSearcher.UpdateSearchResult}
 * that can be created by an {@link UpdateSearcher}.
 * <p>
 *
 * <pre>{@code
 * UpdateSearcher searcher = new UpdateSearcher(new VersionInfo("0.0.0"));
 * UpdateSearcher.UpdateSearchResult result = searcher.search();
 *
 * UpdateActivity updateActivity = new UpdateActivity(context, result);
 * updateActivity.show();
 * }</pre>
 *
 * @author Daniel Gyorffy
 */
public class UpdateActivity {

    private final Context context;
    private final UpdateSearcher.UpdateSearchResult updateSearchResult;

    /**
     * Creates a basic {@link UpdateActivity}.
     *
     * @param context            for displaying the dialogs.
     * @param updateSearchResult the actual update-search data
     */
    public UpdateActivity(@NotNull Context context,
                          @NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) {
        this.context = context;
        this.updateSearchResult = updateSearchResult;
    }

    /**
     * Shows the activity through the particular {@link Context}.
     *
     * <p>
     * It will display an error-dialog if the program couldn't search for updates because of an error.
     * <p>
     * This method actually calls the {@link #show(boolean)} method with a {@code true} value.
     *
     * @see #show(boolean)
     */
    public void show() {
        this.show(true);
    }

    /**
     * Shows the activity through the particular {@link Context}.
     *
     * @param showFeedbackDialog it should be {@code true} if we want an error message if the update-searching failed or
     *                           a feedback message if there is no new update available;
     *                           {@code false} if we only want to show if an update is available
     */
    public void show(boolean showFeedbackDialog) {
        updateSearchResult.ifFailed(exception -> {
            if (showFeedbackDialog) {
                context.showErrorDialog(
                        I18N.getValue("update.failed.title"),
                        I18N.getValue("update.failed.msg"),
                        exception, buttonType -> {
                            //empty
                        });
            }
        }).ifNewUpdateAvailable(updateInformation -> {
            UpdateDialog updateDialog = new UpdateDialog(context, updateInformation,
                    (context, updDialog) -> context.hideOverlay((Region) updDialog.getParent().getParent()));
            this.context.showOverlay(new StackPane(new Group(updateDialog)), true);
        }).ifNoUpdateAvailable(updateInformation -> {
            if (showFeedbackDialog) {
                context.showInformationDialog(
                        I18N.getValue("update.up_to_date.title"),
                        I18N.getValue("update.up_to_date.msg"),
                        buttonType -> {
                            //empty
                        });
            }
        });
    }
}
