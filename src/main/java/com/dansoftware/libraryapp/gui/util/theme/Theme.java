package com.dansoftware.libraryapp.gui.util.theme;

import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * A Theme can change the appearance of the GUI.
 */
public class Theme {

    /**
     * Specifies a dark theme
     */
    public static final Theme DARK = new Theme(
            Collections.singletonList("/com/dansoftware/libraryapp/gui/util/theme/global-dark.css"),
            scene -> new JMetro(Style.DARK).setScene(scene)
    );

    /**
     * Specifies a light theme
     */
    public static final Theme LIGHT = new Theme(
            Collections.singletonList("/com/dansoftware/libraryapp/gui/util/theme/global-light.css"),
            scene -> new JMetro(Style.LIGHT).setScene(scene)
    );

    private final List<String> stylesheets;
    private final Consumer<Scene> onSceneApplier;

    /**
     * Creates an applicable theme.
     *
     * @param stylesheets a list of stylesheets on the class-path; mustn't be null
     * @param onSceneApplier an consumer that can later apply custom operations on a scene;
     *                       can be null
     * @throws NullPointerException if the list of stylesheets is null
     */
    public Theme(List<String> stylesheets, Consumer<Scene> onSceneApplier) {
        this.stylesheets = Objects.requireNonNull(stylesheets, "The list of stylesheets mustn't be null");
        this.onSceneApplier = onSceneApplier;
    }

    public void apply(Scene scene) {
        Objects.requireNonNull(scene, "The scene mustn't be null");

        if (nonNull(this.onSceneApplier)) this.onSceneApplier.accept(scene);

        scene.getStylesheets().addAll(stylesheets);
    }

}
