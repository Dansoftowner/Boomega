package com.dansoftware.libraryapp.gui.context;

import com.dansoftware.libraryapp.gui.util.BaseFXUtils;
import com.dansoftware.libraryapp.gui.util.ExceptionDisplayPane;
import com.dansoftware.libraryapp.gui.util.I18NButtonTypes;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.WorkbenchSkin;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.WorkbenchView;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Encapsulates a {@link Workbench} into a {@link Context} implementation.
 * <p>
 * Used by {@link Context#from(Workbench)}.
 *
 * @author Daniel Gyorffy
 */
final class WorkbenchContextAdapter implements Context {

    private static final Logger logger = LoggerFactory.getLogger(WorkbenchContextAdapter.class);

    private final Workbench workbench;
    private final NotificationsBox notificationsBox;

    private TaskbarProgressbar taskbarProgressbarCache;


    WorkbenchContextAdapter(@NotNull Workbench workbench) {
        this.workbench = workbench;
        this.notificationsBox = new NotificationsBox();
        initToWorkbenchView(workbench, notificationsBox);
    }

    private void initToWorkbenchView(Workbench workbench, Node child) {
        final ObjectProperty<Skin<?>> skinProperty = workbench.skinProperty();
        skinProperty.addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> skin) {
                if (skin != null) {
                    try {
                        final var workbenchSkin = (WorkbenchSkin) skin;
                        Class<? extends WorkbenchSkin> workbenchSkinClass = workbenchSkin.getClass();
                        Field workbenchViewField = workbenchSkinClass.getDeclaredField("workbenchView");
                        workbenchViewField.setAccessible(true);
                        final var workbenchView = (WorkbenchView) workbenchViewField.get(skin);
                        workbenchView.getChildren().add(child);
                    } catch (RuntimeException | ReflectiveOperationException e) {
                        logger.error("Couldn't retrieve WorkbenchView", e);
                    }
                    observable.removeListener(this);
                }
            }
        });
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
        return ContextDialog.from(workbench.showDialog(buildErrorDialog(title, message, exception, onResult)), ContextDialog.Type.ERROR);
    }

    @Override
    public @NotNull ContextDialog showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return ContextDialog.from(workbench.showDialog(buildInformationDialog(title, message, onResult)), ContextDialog.Type.INFORMATION);
    }

    @Override
    public ContextDialog showConfirmationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return ContextDialog.from(workbench.showDialog(buildConfirmationDialog(title, message, onResult)), ContextDialog.Type.CONFIRMATION);
    }

    @Override
    public @NotNull ContextDialog showDialog(String title, Node content, Consumer<ButtonType> onResult, ButtonType... buttonTypes) {
        return ContextDialog.from(workbench.showDialog(WorkbenchDialog.builder(title, content, buttonTypes).onResult(onResult).build()));
    }

    @Override
    public ButtonType showErrorDialogAndWait(String title, String message) {
        final var key = new Object();
        showErrorDialog(title, message, buttonType -> Platform.exitNestedEventLoop(key, buttonType));
        return (ButtonType) Platform.enterNestedEventLoop(key);
    }

    @Override
    public ButtonType showErrorDialogAndWait(String title, String message, Exception e) {
        final var key = new Object();
        showErrorDialog(title, message, e, buttonType -> Platform.exitNestedEventLoop(key, buttonType));
        return (ButtonType) Platform.enterNestedEventLoop(key);
    }

    @Override
    public ButtonType showInformationDialogAndWait(String title, String message) {
        final var key = new Object();
        showInformationDialog(title, message, buttonType -> Platform.exitNestedEventLoop(key, buttonType));
        return (ButtonType) Platform.enterNestedEventLoop(key);
    }

    @Override
    public ButtonType showConfirmationDialogAndWait(String title, String message) {
        final var key = new Object();
        showConfirmationDialog(title, message, buttonType -> Platform.exitNestedEventLoop(key, buttonType));
        return (ButtonType) Platform.enterNestedEventLoop(key);
    }

    @Override
    public ButtonType showDialogAndWait(String title, Node content, ButtonType... buttonTypes) {
        final var key = new Object();
        this.showDialog(title, content, buttonType -> Platform.exitNestedEventLoop(key, buttonType), buttonTypes);
        return (ButtonType) Platform.enterNestedEventLoop(key);
    }

    private NotificationNode buildNotificationNode(NotificationNode.NotificationType type,
                                                   String title,
                                                   String message,
                                                   Consumer<NotificationNode> closeAction,
                                                   EventHandler<MouseEvent> onClicked) {
        final var notificationNode = new NotificationNode(type, title, message, closeAction);
        StackPane.setAlignment(notificationNode, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(notificationNode, new Insets(0, 10, 10, 0));
        notificationNode.setOnMouseClicked(event -> {
            if (onClicked != null) {
                onClicked.handle(event);
            }
            closeAction.accept(notificationNode);
        });
        return notificationNode;
    }

    private void showNotification(NotificationNode.NotificationType type,
                                  String title,
                                  String message,
                                  Duration duration,
                                  EventHandler<MouseEvent> onClicked) {
        final NotificationNode notificationNode = buildNotificationNode(type, title, message, notificationsBox::removeItem, onClicked);
        notificationsBox.pushItem(notificationNode, duration);
        playNotificationSound();
    }

    private void playNotificationSound() {
        Optional.ofNullable(Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.hand"))
                .map(it -> (Runnable) it)
                .ifPresent(Runnable::run);
    }

    @Override
    public void showErrorNotification(String title, String message) {
        showNotification(NotificationNode.NotificationType.ERROR, title, message, null, null);
    }

    @Override
    public void showErrorNotification(String title, String message, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.ERROR, title, message, null, onClicked);
    }

    @Override
    public void showErrorNotification(String title, String message, Duration duration) {
        showNotification(NotificationNode.NotificationType.ERROR, title, message, duration, null);
    }

    @Override
    public void showErrorNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.ERROR, title, message, duration, onClicked);
    }

    @Override
    public void showWarningNotification(String title, String message) {
        showNotification(NotificationNode.NotificationType.WARNING, title, message, null, null);
    }

    @Override
    public void showWarningNotification(String title, String message, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.WARNING, title, message, null, onClicked);
    }

    @Override
    public void showWarningNotification(String title, String message, Duration duration) {
        showNotification(NotificationNode.NotificationType.WARNING, title, message, duration, null);
    }

    @Override
    public void showWarningNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.WARNING, title, message, duration, onClicked);
    }

    @Override
    public void showInformationNotification(String title, String message) {
        showNotification(NotificationNode.NotificationType.INFO, title, message, null, null);
    }

    @Override
    public void showInformationNotification(String title, String message, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.INFO, title, message, null, onClicked);
    }

    @Override
    public void showInformationNotification(String title, String message, Duration duration) {
        showNotification(NotificationNode.NotificationType.INFO, title, message, duration, null);
    }

    @Override
    public void showInformationNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {
        showNotification(NotificationNode.NotificationType.INFO, title, message, duration, onClicked);
    }

    @Override
    public void showModule(@NotNull Class<?> classRef) {
        workbench.getModules().stream()
                .filter(module -> module.getClass().equals(classRef))
                .findFirst()
                .ifPresent(workbench::openModule);
    }

    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    @Override
    public <D> void showModule(@NotNull Class<? extends NotifiableModule<D>> classRef, D data) {
        workbench.getModules().stream()
                .filter(module -> module.getClass().equals(classRef))
                .map(module -> (WorkbenchModule & NotifiableModule<D>) module)
                .findFirst()
                .ifPresent(module -> {
                    workbench.openModule(module);
                    module.commitData(data);
                });
    }

    @Override
    public Scene getContextScene() {
        return workbench.getScene();
    }

    @Override
    public Window getContextWindow() {
        return WindowUtils.getWindowOf(workbench);
    }

    @Override
    public void requestFocus() {
        final Window contextWindow = getContextWindow();
        if (contextWindow != null)
            contextWindow.requestFocus();
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
        final Window contextWindow = getContextWindow();
        return contextWindow != null && contextWindow.isShowing();
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
    public void onWindowPresent(Consumer<Window> action) {
        BaseFXUtils.onWindowPresent(workbench, action);
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
        if (exception != null) {
            final ExceptionDisplayPane content = new ExceptionDisplayPane(exception);
            return WorkbenchDialog.builder(title, new VBox(new Label(message), content) {{
                setSpacing(10);
            }}, I18NButtonTypes.OK)
                    .onResult(onResult)
                    .build();
        }
        return WorkbenchDialog.builder(title, message, I18NButtonTypes.OK)
                .onResult(onResult)
                .build();

    }

    private static WorkbenchDialog buildInformationDialog(@NotNull String title,
                                                          @NotNull String message,
                                                          @NotNull Consumer<ButtonType> onResult) {
        return WorkbenchDialog.builder(title, message, I18NButtonTypes.OK)
                .onResult(onResult)
                .build();
    }

    private static WorkbenchDialog buildConfirmationDialog(@NotNull String title,
                                                           @NotNull String message,
                                                           @NotNull Consumer<ButtonType> onResult) {
        return WorkbenchDialog.builder(title, message, I18NButtonTypes.NO, I18NButtonTypes.YES).onResult(onResult).build();
    }
}
