package com.dansoftware.libraryapp.gui.util;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class UIUtils {

    private UIUtils() {
    }

    public static <T> void refreshComboBox(ComboBox<T> comboBox) {
        ObservableList<T> items = comboBox.getItems();
        T selected = comboBox.getSelectionModel().getSelectedItem();
        comboBox.setItems(null);
        comboBox.setItems(items);
        comboBox.getSelectionModel().select(selected);
    }
}
