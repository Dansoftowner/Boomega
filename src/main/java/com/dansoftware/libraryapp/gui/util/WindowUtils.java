package com.dansoftware.libraryapp.gui.util;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @NotNull
    public static Optional<Window> getWindowOptionalOf(@Nullable Node node) {
        if (Objects.isNull(node) || Objects.isNull(node.getScene()))
            return Optional.empty();

        return Optional.ofNullable(node.getScene().getWindow());
    }

    @NotNull
    public static Optional<Stage> getStageOptionalOf(@Nullable Node node) {
        return getWindowOptionalOf(node).map(window -> (Stage) window);
    }

    /**
     * Returns all {@link Stage}s that is owned by the given {@link Stage}.
     *
     * @param stage the stage that we want to find the windows of
     * @return the {@link List} of {@link Stage} objects
     */
    public static List<Stage> getOwnedStagesOf(@Nullable Stage stage) {
        if (stage == null) return Collections.emptyList();
        return Window.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .map(window -> (Stage) window)
                .filter(window -> window.getOwner().equals(stage))
                .collect(Collectors.toList());
    }
}
