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

    private static Theme DEFAULT;

    /**
     * Specifies a dark theme
     */
    public static final Theme DARK = new Theme(
            new ThemeIdentifier("dark", "theme.dark"),
            Collections.singletonList("/com/dansoftware/libraryapp/gui/util/theme/global-dark.css"),
            scene -> new JMetro(Style.DARK).setScene(scene)
    );

    /**
     * Specifies a light theme
     */
    public static final Theme LIGHT = new Theme(
            new ThemeIdentifier("light", "theme.light"),
            Collections.singletonList("/com/dansoftware/libraryapp/gui/util/theme/global-light.css"),
            scene -> new JMetro(Style.LIGHT).setScene(scene)
    );

    private final ThemeIdentifier identifier;
    private final List<String> stylesheets;
    private final Consumer<Scene> onSceneApplier;

    /**
     * Creates an applicable theme.
     *
     * @param identifier     the identifier of the theme; it is used to define the name of the theme
     *                       and the internationalized name (Resource Bundle key) of the theme name;
     *                       mustn't be null
     * @param stylesheets    a list of stylesheets on the class-path; mustn't be null
     * @param onSceneApplier an consumer that can later apply custom operations on a scene;
     *                       can be null
     * @throws NullPointerException if the list of stylesheets or the identifier is null
     */
    public Theme(ThemeIdentifier identifier, List<String> stylesheets, Consumer<Scene> onSceneApplier) {
        this.identifier = Objects.requireNonNull(identifier, "The identifier mustn't be null");
        this.stylesheets = Objects.requireNonNull(stylesheets, "The list of stylesheets mustn't be null");
        this.onSceneApplier = onSceneApplier;
    }

    /**
     * Applies the theme on the particular scene.
     *
     * <p>
     * This method <b>will delete all previous stylesheets</b>
     * before applying the new theme on the scene
     *
     * @param scene the scene to apply the theme to; must not be null
     * @throws NullPointerException if the scene is null
     */
    public void apply(Scene scene) {
        Objects.requireNonNull(scene, "The scene mustn't be null");

        scene.getStylesheets().clear();
        if (nonNull(this.onSceneApplier)) this.onSceneApplier.accept(scene);

        scene.getStylesheets().addAll(stylesheets);
    }

    public ThemeIdentifier getIdentifier() {
        return identifier;
    }

    public List<String> getStylesheets() {
        return stylesheets;
    }

    public Consumer<Scene> getOnSceneApplier() {
        return onSceneApplier;
    }

    public static Theme getDefault() {
        if (DEFAULT == null)
            return LIGHT;
        return DEFAULT;
    }

    public static void setDefault(Theme theme) {
        Theme.DEFAULT = theme;
    }

    public static void applyDefault(Scene scene) {
        Theme.getDefault().apply(scene);
    }

    public static final class ThemeIdentifier {
        private final String id;
        private final String bundleID;

        public ThemeIdentifier(String id, String bundleID) {
            this.id = id;
            this.bundleID = bundleID;
        }

        public String getId() {
            return id;
        }

        public String getBundleID() {
            return bundleID;
        }
    }

}
