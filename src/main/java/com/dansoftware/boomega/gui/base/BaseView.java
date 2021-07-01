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

package com.dansoftware.boomega.gui.base;

import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.api.ContextDialog;
import com.dansoftware.boomega.gui.api.NotifiableModule;
import com.dansoftware.boomega.gui.control.ExceptionDisplayPane;
import com.dansoftware.boomega.gui.keybinding.KeyBinding;
import com.dansoftware.boomega.gui.util.BaseFXUtils;
import com.dansoftware.boomega.gui.util.I18NButtonTypes;
import com.dansoftware.boomega.gui.util.WindowUtils;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.WorkbenchSkin;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.WorkbenchView;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A {@link BaseView} wraps a UI element and adds extra functionality (like showing dialogs, overlays etc...)
 * to it by implementing the {@link Context} interface.
 *
 * @see Context
 */
public class BaseView extends StackPane implements Context {

    private static final Logger logger = LoggerFactory.getLogger(BaseView.class);

    private final Workbench workbench;
    private final NotificationsBox notificationsBox;
    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
    private TaskbarProgressbar taskbarProgressbarCache;

    public BaseView() {
        this.notificationsBox = new NotificationsBox();
        this.workbench = buildWorkbench();
        this.getChildren().add(workbench);
    }

    public BaseView(@Nullable Node content) {
        this();
        this.setContent(content);
    }

    private Workbench buildWorkbench() {
        return initWorkbench(
                Workbench.builder(
                        new WorkbenchModule(null, (Image) null) {
                            @Override
                            public Node activate() {
                                return content.get();
                            }
                        }
                ).build()
        );
    }

    private Workbench initWorkbench(Workbench workbench) {
        workbench.skinProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> skin) {
                if (skin != null) {
                    try {
                        final var workbenchSkin = (WorkbenchSkin) skin;
                        Class<? extends WorkbenchSkin> workbenchSkinClass = workbenchSkin.getClass();
                        Field workbenchViewField = workbenchSkinClass.getDeclaredField("workbenchView");
                        workbenchViewField.setAccessible(true);
                        final var workbenchView = (WorkbenchView) workbenchViewField.get(skin);
                        workbenchView.getChildren().add(notificationsBox);
                        Node toolbar = skin.getNode().lookup("#toolbar");
                        toolbar.setVisible(false);
                        toolbar.setManaged(false);
                    } catch (RuntimeException | ReflectiveOperationException e) {
                        logger.error("Couldn't retrieve WorkbenchView", e);
                    }
                    observable.removeListener(this);
                }
            }
        });
        return workbench;
    }

    public Node getContent() {
        return content.get();
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public ObservableList<Region> getBlockingOverlaysShown() {
        return workbench.getBlockingOverlaysShown();
    }

    public ObservableList<Region> getNonBlockingOverlaysShown() {
        return workbench.getNonBlockingOverlaysShown();
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
        return new WorkbenchDialogContextDialog(workbench.showDialog(buildErrorDialog(title, message, exception, onResult)), ContextDialog.Type.ERROR);
    }

    @Override
    public @NotNull ContextDialog showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return new WorkbenchDialogContextDialog(workbench.showDialog(buildInformationDialog(title, message, onResult)), ContextDialog.Type.INFORMATION);
    }

    @Override
    public ContextDialog showConfirmationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return new WorkbenchDialogContextDialog(workbench.showDialog(buildConfirmationDialog(title, message, onResult)), ContextDialog.Type.CONFIRMATION);
    }

    @Override
    public @NotNull ContextDialog showDialog(String title, Node content, Consumer<ButtonType> onResult, ButtonType... buttonTypes) {
        return new WorkbenchDialogContextDialog(workbench.showDialog(WorkbenchDialog.builder(title, content, buttonTypes).onResult(onResult).build()));
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
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <D> void notifyModule(@NotNull Class<? extends NotifiableModule<D>> classRef, D data) {
        workbench.getModules().stream()
                .filter(module -> module.getClass().equals(classRef))
                .map(module -> (WorkbenchModule & NotifiableModule<D>) module)
                .findFirst()
                .ifPresent(module -> module.commitData(data));
    }

    @Override
    public void addKeyBindingDetection(@NotNull KeyBinding keyBinding, Consumer<KeyBinding> onDetected) {
        final Consumer<Scene> action = (scene) -> {
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (keyBinding.match(event)) {
                    onDetected.accept(keyBinding);
                }
            });
        };

        final Scene scene = getContextScene();
        if (scene != null) {
            action.accept(scene);
        } else {
            workbench.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                    if (newValue != null) {
                        action.accept(newValue);
                        observable.removeListener(this);
                    }
                }
            });
        }
    }

    @Override
    public Scene getContextScene() {
        return workbench.getScene();
    }

    @Override
    public javafx.stage.Window getContextWindow() {
        return WindowUtils.getWindowOf(workbench);
    }

    @Override
    public void focusRequest() {
        final javafx.stage.Window contextWindow = getContextWindow();
        if (contextWindow != null)
            contextWindow.requestFocus();
    }

    @Override
    public void toFrontRequest() {
        javafx.stage.Window contextWindow = getContextWindow();
        if (contextWindow instanceof Stage) {
            Stage stage = (Stage) contextWindow;
            stage.setIconified(false);
            stage.toFront();
        }
    }

    @Override
    public boolean isShowing() {
        final javafx.stage.Window contextWindow = getContextWindow();
        return contextWindow != null && contextWindow.isShowing();
    }

    @Override
    public void showIndeterminateProgress() {
        workbench.setCursor(javafx.scene.Cursor.WAIT);
        getTaskbarProgressbar().showIndeterminateProgress();
    }

    @Override
    public void showProgress(long done, long max, @NotNull Context.ProgressType type) {
        workbench.setCursor(javafx.scene.Cursor.DEFAULT);
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

    /**
     * Wraps a {@link WorkbenchDialog} into a {@link ContextDialog} implementation.
     *
     * @author Daniel Gyorffy
     */
    private static class WorkbenchDialogContextDialog implements ContextDialog {

        private static final String DIALOG_STYLE_CLASS = "alertDialog";

        private final WorkbenchDialog workbenchDialog;
        private final Type type;

        WorkbenchDialogContextDialog(@NotNull WorkbenchDialog workbenchDialog, @Nullable Type type) {
            this.workbenchDialog = Objects.requireNonNull(workbenchDialog);
            this.workbenchDialog.getStyleClass().add(DIALOG_STYLE_CLASS);
            this.type = workbenchDialog.getType() != null ? Type.valueOf(workbenchDialog.getType().toString()) : type;
        }

        WorkbenchDialogContextDialog(@NotNull WorkbenchDialog workbenchDialog) {
            this(workbenchDialog, null);
        }

        @Override
        public ObjectProperty<EventHandler<javafx.event.Event>> onShownProperty() {
            return workbenchDialog.onShownProperty();
        }

        @Override
        public void setOnShown(EventHandler<javafx.event.Event> value) {
            workbenchDialog.setOnShown(value);
        }

        @Override
        public EventHandler<javafx.event.Event> getOnShown() {
            return workbenchDialog.getOnShown();
        }

        @Override
        public ObjectProperty<EventHandler<javafx.event.Event>> onHiddenProperty() {
            return workbenchDialog.onHiddenProperty();
        }

        @Override
        public void setOnHidden(EventHandler<javafx.event.Event> value) {
            workbenchDialog.setOnHidden(value);
        }

        @Override
        public EventHandler<Event> getOnHidden() {
            return workbenchDialog.getOnHidden();
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public ObservableList<ButtonType> getButtonTypes() {
            return workbenchDialog.getButtonTypes();
        }

        @Override
        public BooleanProperty maximizedProperty() {
            return workbenchDialog.maximizedProperty();
        }

        @Override
        public void setMaximized(boolean max) {
            workbenchDialog.setMaximized(max);
        }

        @Override
        public boolean isMaximized() {
            return workbenchDialog.isMaximized();
        }

        @Override
        public ObjectProperty<Node> contentProperty() {
            return workbenchDialog.contentProperty();
        }

        @Override
        public void setContent(Node content) {
            workbenchDialog.setContent(content);
        }

        @Override
        public Node getContent() {
            return workbenchDialog.getContent();
        }

        @Override
        public StringProperty titleProperty() {
            return workbenchDialog.titleProperty();
        }

        @Override
        public String getTitle() {
            return workbenchDialog.getTitle();
        }

        @Override
        public void setTitle(String title) {
            workbenchDialog.setTitle(title);
        }

        @Override
        public ObservableList<String> getStyleClass() {
            return workbenchDialog.getStyleClass();
        }

        @Override
        public ObjectProperty<Exception> exceptionProperty() {
            return workbenchDialog.exceptionProperty();
        }

        @Override
        public void setException(Exception ex) {
            workbenchDialog.setException(ex);
        }

        @Override
        public Exception getException() {
            return workbenchDialog.getException();
        }

        @Override
        public String getDetails() {
            return workbenchDialog.getDetails();
        }

        @Override
        public StringProperty detailsProperty() {
            return workbenchDialog.detailsProperty();
        }

        @Override
        public void setDetails(String details) {
            workbenchDialog.setDetails(details);
        }

        @Override
        public BooleanProperty blockingProperty() {
            return workbenchDialog.blockingProperty();
        }

        @Override
        public void setBlocking(boolean blocking) {
            workbenchDialog.setBlocking(blocking);
        }

        @Override
        public boolean isBlocking() {
            return workbenchDialog.isBlocking();
        }

        @Override
        public Consumer<ButtonType> getOnResult() {
            return workbenchDialog.getOnResult();
        }

        @Override
        public ObjectProperty<Consumer<ButtonType>> onResultProperty() {
            return workbenchDialog.onResultProperty();
        }

        @Override
        public void setOnResult(Consumer<ButtonType> onResult) {
            workbenchDialog.setOnResult(onResult);
        }

        @Override
        public ReadOnlyBooleanProperty showingProperty() {
            return workbenchDialog.showingProperty();
        }

        @Override
        public boolean isShowing() {
            return workbenchDialog.isShowing();
        }
    }

}
