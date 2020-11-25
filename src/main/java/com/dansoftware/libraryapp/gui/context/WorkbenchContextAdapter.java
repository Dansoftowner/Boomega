package com.dansoftware.libraryapp.gui.context;

import com.dansoftware.libraryapp.gui.util.I18NButtonTypes;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Encapsulates a {@link Workbench} into a {@link Context} implementation.
 * <p>
 * Used by {@link Context#from(Workbench)}.
 *
 * @author Daniel Gyorffy
 */
final class WorkbenchContextAdapter implements Context {

    private final Workbench workbench;

    private TaskbarProgressbar taskbarProgressbarCache;

    WorkbenchContextAdapter(@NotNull Workbench workbench) {
        this.workbench = workbench;
    }

    @Override
    public void showOverlay(Region region, boolean blocking) {
        workbench.showOverlay(region, blocking);
    }

    @Override
    public void hideOverlay(Region region) {
        workbench.hideOverlay(region);
    }

    @Override
    public @NotNull ContextDialog showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        return showErrorDialog(title, message, null, onResult);
    }

    @Override
    public @NotNull ContextDialog showErrorDialog(String title, String message, Exception exception, Consumer<ButtonType> onResult) {
        return ContextDialog.from(workbench.showErrorDialog(title, message, exception, onResult));
    }

    @Override
    public @NotNull ContextDialog showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return ContextDialog.from(workbench.showInformationDialog(title, message, onResult));
    }

    @Override
    public @NotNull ContextDialog showDialog(String title, Node content, Consumer<ButtonType> onResult, ButtonType... buttonTypes) {
        return ContextDialog.from(workbench.showDialog(WorkbenchDialog.builder(title, content, buttonTypes).onResult(onResult).build()));
    }

    @Override
    public Window getContextWindow() {
        return WindowUtils.getWindowOf(workbench);
    }

    @Override
    public void requestFocus() {
        getContextWindow().requestFocus();
    }

    @Override
    public void toFront() {
        Window contextWindow = getContextWindow();
        if (contextWindow instanceof Stage) {
            Stage stage = (Stage) contextWindow;
            stage.setIconified(false);
            stage.toFront();
        }
    }

    @Override
    public boolean isShowing() {
        return getContextWindow().isShowing();
    }

    @Override
    public void showIndeterminateProgress() {
        workbench.setCursor(Cursor.WAIT);
        getTaskbarProgressbar().showIndeterminateProgress();
    }

    @Override
    public void showProgress(long done, long max, @NotNull ProgressType type) {
        workbench.setCursor(Cursor.DEFAULT);
        getTaskbarProgressbar().showCustomProgress(done, max, TaskbarProgressbar.Type.valueOf(type.name()));
    }

    @Override
    public void stopProgress() {
        workbench.setCursor(Cursor.DEFAULT);
        getTaskbarProgressbar().stopProgress();
    }

    private TaskbarProgressbar getTaskbarProgressbar() {
        if (taskbarProgressbarCache == null) {
            taskbarProgressbarCache = TaskbarProgressbarFactory.getTaskbarProgressbar((Stage) getContextWindow());
        }
        return taskbarProgressbarCache;
    }

    private static WorkbenchDialog buildErrorDialog(@NotNull String title,
                                                    @NotNull String message,
                                                    @Nullable Exception exception,
                                                    @NotNull Consumer<ButtonType> onResult) {
        return WorkbenchDialog.builder(title, message, I18NButtonTypes.CLOSE)
                .exception(exception)
                .onResult(onResult)
                .build();
    }

    private static WorkbenchDialog buildInformationDialog(@NotNull String title,
                                                          @NotNull String message,
                                                          @NotNull Consumer<ButtonType> onResult) {
        return WorkbenchDialog.builder(title, message, I18NButtonTypes.OK)
                .build();
    }
}
