package com.dansoftware.libraryapp.gui.util;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WindowUtils {

    private WindowUtils() {
    }

    /**
     * Finds the corresponding {@link Window} to the particular {@link Node} object.
     *
     * @param node the node to find the window of
     * @return the {@link Window} object
     */
    @Nullable
    public static Window getWindowOf(@Nullable Node node) {
        if (Objects.isNull(node) || Objects.isNull(node.getScene()))
            return null;

        return node.getScene().getWindow();
    }

    /**
     * @see #getWindowOf(Node)
     */
    @Nullable
    public static Stage getStageOf(Node node) {
        return (Stage) getWindowOf(node);
    }
}
