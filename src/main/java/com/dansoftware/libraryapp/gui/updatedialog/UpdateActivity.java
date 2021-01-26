package com.dansoftware.libraryapp.gui.updatedialog;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.i18n.I18N;
import com.dansoftware.libraryapp.update.UpdateSearcher;
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
                        I18N.getUpdateDialogValue("update.activity.failed.title"),
                        I18N.getUpdateDialogValue("update.activity.failed.msg"),
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
                        I18N.getUpdateDialogValue("update.activity.up.to.date.title"),
                        I18N.getUpdateDialogValue("update.activity.up.to.date.msg"),
                        buttonType -> {
                            //empty
                        });
            }
        });
    }
}
