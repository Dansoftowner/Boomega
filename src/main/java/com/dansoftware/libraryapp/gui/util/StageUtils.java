package com.dansoftware.libraryapp.gui.util;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

public class StageUtils {

    private StageUtils() {
    }

    public static Window getWindowOf(Node node) {
        if (Objects.isNull(node) || Objects.isNull(node.getScene()))
            return null;

        return node.getScene().getWindow();
    }

    public static Stage getStageOf(Node node) {
        return (Stage) getWindowOf(node);
    }
}
