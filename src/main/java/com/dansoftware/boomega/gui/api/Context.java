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

package com.dansoftware.boomega.gui.api;

import com.dansoftware.boomega.gui.keybinding.KeyBinding;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A Context is an abstract structure that allows to interact with a particular GUI environment
 * <i>(making alerts, showing notifications, etc...)</i>.
 *
 * @author Daniel Gyorffy
 */
public interface Context {

    /* Overlays */

    /**
     * Shows a popup (on the center) with the GUI-element defined.
     *
     * @param region the {@link Region} GUI element to be shown
     */
    default void showOverlay(Region region) {
        showOverlay(region, false);
    }

    /**
     * Shows a popup (on the center) with the GUI-element defined.
     *
     * @param region   the {@link Region} GUI element to be shown
     * @param blocking {@code false} if clicking outside of the popup should close it.
     */
    void showOverlay(Region region, boolean blocking);

    /**
     * Hides the popup (if it's showing).
     *
     * @param region to be hidden
     */
    void hideOverlay(Region region);

    /* Dialogs */

    /**
     * Shows an error alert dialog.
     *
     * @param title    the title of the dialog
     * @param message  the message of the dialog
     * @param onResult the action when the user clicks the OK button
     */
    @NotNull
    ContextDialog showErrorDialog(String title,
                                  String message,
                                  Consumer<ButtonType> onResult);

    /**
     * Shows an error alert dialog. Also, it can show
     * the stacktrace of an exception.
     *
     * @param title     the title of the dialog
     * @param message   the message of the dialog
     * @param exception the exception that caused the dialog
     * @param onResult  the action when the user clicks the OK button
     */
    @NotNull
    ContextDialog showErrorDialog(String title,
                                  String message,
                                  Exception exception,
                                  Consumer<ButtonType> onResult);

    /**
     * Shows an information dialog.
     *
     * @param title    the title
     * @param message  the message
     * @param onResult the action that handles the button click-s on the dialog
     */
    @NotNull
    ContextDialog showInformationDialog(String title,
                                        String message,
                                        Consumer<ButtonType> onResult);

    ContextDialog showConfirmationDialog(String title,
                                         String message,
                                         Consumer<ButtonType> onResult);

    @NotNull
    ContextDialog showDialog(String title, Node content, Consumer<ButtonType> onResult, ButtonType... buttonTypes);

    ButtonType showErrorDialogAndWait(String title, String message);

    ButtonType showErrorDialogAndWait(String title, String message, Exception e);

    ButtonType showInformationDialogAndWait(String title, String message);

    ButtonType showConfirmationDialogAndWait(String title, String message);

    ButtonType showDialogAndWait(String title, Node content, ButtonType... buttonTypes);

    /* Notifications */

    void showErrorNotification(String title, String message);

    void showErrorNotification(String title, String message, EventHandler<MouseEvent> onClicked);

    void showErrorNotification(String title, String message, Duration duration);

    void showErrorNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked);

    void showWarningNotification(String title, String message);

    void showWarningNotification(String title, String message, EventHandler<MouseEvent> onClicked);

    void showWarningNotification(String title, String message, Duration duration);

    void showWarningNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked);

    void showInformationNotification(String title, String message);

    void showInformationNotification(String title, String message, EventHandler<MouseEvent> onClicked);

    void showInformationNotification(String title, String message, EventHandler<MouseEvent> onClicked, Hyperlink... hyperlinks);

    void showInformationNotification(String title, String message, Duration duration);

    void showInformationNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked);

    /* *** */

    default void sendRequest(@NotNull Request request) {
    }

    void addKeyBindingDetection(@NotNull KeyBinding keyBinding, Consumer<KeyBinding> onDetected);

    /* *** */

    @Nullable
    Scene getContextScene();

    @Nullable
    Window getContextWindow();

    /**
     * Requests focus on the particular GUI environment (usually on a {@link javafx.stage.Window}).
     */
    void focusRequest();

    /**
     * Brings the particular GUI environment to front (usually with the {@link javafx.stage.Stage#toFront()})
     */
    void toFrontRequest();

    boolean isShowing();

    void showIndeterminateProgress();

    void stopProgress();

    void showProgress(long done, long max, @NotNull ProgressType type);

    void onWindowPresent(Consumer<Window> action);

    /**
     * Returns {@code true} if the {@link Context} is actually performs actions on a particular gui.
     */
    default boolean isReachable() {
        return true;
    }

    default void close() {
        Window window = getContextWindow();
        if (window instanceof Stage asStage)
            asStage.close();
        else if (window != null)
            window.hide();
    }

    default void showErrorDialog(String title, String message) {
        this.showErrorDialog(title, message, buttonType -> {
        });
    }

    default void showErrorDialog(String title, String message, Exception e) {
        this.showErrorDialog(title, message, e, buttonType -> {
        });
    }

    static Context empty() {
        return new EmptyContext() {
        };
    }

    enum ProgressType {
        ERROR,
        NORMAL,
        PAUSED
    }

    interface Request {
    }
}
