package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
 * an {@link com.dansoftware.libraryapp.update.UpdateSearcher.UpdateSearchResult}
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
     *                  {@code false} if we only want to show if an update is available
     */
    public void show(boolean showFeedbackDialog) {
        updateSearchResult.ifFailed(exception -> {
            if (showFeedbackDialog) {
                context.showErrorDialog(
                        I18N.getAlertMsg("update.activity.failed.title"),
                        I18N.getAlertMsg("update.activity.failed.msg"),
                        exception, buttonType -> {
                            //empty
                        });
            }
        }).ifNewUpdateAvailable(updateInformation -> {
            UpdateView.HideStrategy hideStrategy = (context, view) ->
                    context.hideOverlay((Region) view.getParent().getParent());
            UpdateView updateView = new UpdateView(this.context, hideStrategy, updateInformation);
            this.context.showOverlay(new StackPane(new Group(updateView)), true);
        }).ifNoUpdateAvailable(updateInformation -> {
            if (showFeedbackDialog) {
                context.showInformationDialog(
                        I18N.getAlertMsg("update.activity.up.to.date.title"),
                        I18N.getAlertMsg("update.activity.up.to.date.msg"),
                        buttonType -> {
                            //empty
                        });
            }
        });
    }
}
