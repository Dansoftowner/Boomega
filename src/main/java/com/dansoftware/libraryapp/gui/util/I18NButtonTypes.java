package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * Provides internationalized {@link ButtonType} constants for the app.
 *
 * @author Daniel Gyorffy
 */
public class I18NButtonTypes {

    private I18NButtonTypes() {
    }

    public static final ButtonType APPLY = createButtonType("Dialog.apply.button", ButtonBar.ButtonData.APPLY);
    public static final ButtonType OK = createButtonType("Dialog.ok.button", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL = createButtonType("Dialog.cancel.button", ButtonBar.ButtonData.CANCEL_CLOSE);
    public static final ButtonType CLOSE = createButtonType("Dialog.close.button", ButtonBar.ButtonData.CANCEL_CLOSE);
    public static final ButtonType YES = createButtonType("Dialog.yes.button", ButtonBar.ButtonData.YES);
    public static final ButtonType NO = createButtonType("Dialog.no.button", ButtonBar.ButtonData.NO);
    public static final ButtonType FINISH = createButtonType("Dialog.finish.button", ButtonBar.ButtonData.FINISH);
    public static final ButtonType NEXT = createButtonType("Dialog.next.button", ButtonBar.ButtonData.NEXT_FORWARD);
    public static final ButtonType PREVIOUS = createButtonType("Dialog.previous.button", ButtonBar.ButtonData.BACK_PREVIOUS);

    private static ButtonType createButtonType(String key, ButtonBar.ButtonData buttonData) {
        return new ButtonType(I18N.getButtonTypeValues().getString(key), buttonData);
    }
}
