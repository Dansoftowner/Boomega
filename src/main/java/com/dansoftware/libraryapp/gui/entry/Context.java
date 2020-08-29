package com.dansoftware.libraryapp.gui.entry;

import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

/**
 * A Context is an abstract structure that allows to interact with a particular GUI environment
 * <i>(making alerts, showing notifications, etc...)</i>.
 *
 * @author Daniel Gyorffy
 */
public interface Context {

    /**
     * Shows a popup (on the center) with the GUI-element defined.
     *
     * @param region the {@link Region} GUI element to be shown
     */
    void showOverlay(Region region);

    /**
     * Shows a popup (on the center) with the GUI-element defined.
     *
     * @param region the {@link Region} GUI element to be shown
     * @param blocking {@code false} if clicking outside of the popup should close it.
     */
    void showOverlay(Region region, boolean blocking);

    /**
     * Hides the popup (if it's showing).
     *
     * @param region to be hidden
     */
    void hideOverlay(Region region);

    /**
     * Shows an error alert dialog.
     *
     * @param title the title of the dialog
     * @param message the message of the dialog
     * @param onResult the action when the user clicks the OK button
     */
    void showErrorDialog(String title,
                         String message,
                         Consumer<ButtonType> onResult);

    /**
     * Shows an error alert dialog. Also, it can show
     * the stacktrace of an exception.
     *
     * @param title the title of the dialog
     * @param message the message of the dialog
     * @param exception the exception that caused the dialog
     * @param onResult the action when the user clicks the OK button
     */
    void showErrorDialog(String title,
                         String message,
                         Exception exception,
                         Consumer<ButtonType> onResult);

    /**
     * Shows an information dialog.
     *
     * @param title the title
     * @param message the message
     * @param onResult the action that handles the button click-s on the dialog
     */
    void showInformationDialog(String title,
                               String message,
                               Consumer<ButtonType> onResult);

    /**
     * Requests focus on the particular GUI environment (usually on a {@link javafx.stage.Window}).
     */
    void requestFocus();

    /**
     * Brings the particular GUI environment to front (usually with the {@link javafx.stage.Stage#toFront()})
     */
    void toFront();

    boolean isShowing();

    default void showErrorDialog(String title, String message) {
        this.showErrorDialog(title, message, buttonType -> {
        });
    }

    default void showErrorDialog(String title, String message, Exception e) {
        this.showErrorDialog(title, message, e, buttonType -> {
        });
    }
}
