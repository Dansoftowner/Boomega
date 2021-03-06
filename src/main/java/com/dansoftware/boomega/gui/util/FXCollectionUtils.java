package com.dansoftware.boomega.gui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

@Deprecated
public class FXCollectionUtils {

    private FXCollectionUtils() {
    }

    /**
     * @deprecated use {@link BaseFXUtils#copy(ObservableList)} instead
     */
    @Deprecated
    public static <T> ObservableList<T> copyOf(ObservableList<T> observableList) {
        return FXCollections.observableArrayList(List.copyOf(observableList));
    }

}
