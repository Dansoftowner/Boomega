package com.dansoftware.boomega.gui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class FXCollectionUtils {

    private FXCollectionUtils() {
    }

    public static <T> ObservableList<T> copyOf(ObservableList<T> observableList) {
        return FXCollections.observableArrayList(List.copyOf(observableList));
    }

}
