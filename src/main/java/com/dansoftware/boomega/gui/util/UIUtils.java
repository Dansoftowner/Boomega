package com.dansoftware.boomega.gui.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

@Deprecated
public class UIUtils {

    private UIUtils() {
    }

    /**
     * @deprecated use {@link BaseFXUtils#runOnUiThread(Runnable)} instead
     */
    @Deprecated
    public static void runOnUiThread(Runnable action) {
        if (Platform.isFxApplicationThread()) action.run();
        else Platform.runLater(action);
    }

    /**
     * @deprecated use {@link BaseFXUtils#refresh(ComboBox)} instead
     */
    @Deprecated
    public static <T> void refreshComboBox(ComboBox<T> comboBox) {
        ObservableList<T> items = comboBox.getItems();
        T selected = comboBox.getSelectionModel().getSelectedItem();
        comboBox.setItems(null);
        comboBox.setItems(items);
        comboBox.getSelectionModel().select(selected);
    }
}
