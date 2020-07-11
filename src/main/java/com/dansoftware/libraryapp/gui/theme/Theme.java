package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * A Theme is used for changing the appearance of the GUI.
 *
 * @author Daniel Gyorffy
 */
public class Theme {


    private static Theme DEFAULT;

    /**
     * Specifies a dark theme.
     *
     * <p>
     * Uses JMetro's DARK style and some additional stylesheets.
     *
     * @see JMetro
     * @see jfxtras.styles.jmetro.Style#DARK
     */
    public static final Theme DARK = new Theme(
            new ThemeIdentifier("libraryapp_dark", "theme.dark"),
            Collections.singletonList("/com/dansoftware/libraryapp/gui/theme/global-dark.css"),
            scene -> new JMetro(Style.DARK).setScene(scene),
            parent -> new JMetro(Style.DARK).setParent(parent)
    );

    /**
     * Specifies a light theme
     * <p>
     * Uses JMetro's LIGHT style and some additional stylesheets.
     *
     * @see JMetro
     * @see jfxtras.styles.jmetro.Style#LIGHT
     */
    public static final Theme LIGHT = new Theme(
            new ThemeIdentifier("libraryapp_light", "theme.light"),
            Collections.singletonList("/com/dansoftware/libraryapp/gui/theme/global-light.css"),
            scene -> new JMetro(Style.LIGHT).setScene(scene),
            parent -> new JMetro(Style.LIGHT).setParent(parent)
    );

    private final ThemeIdentifier identifier;
    private final List<String> stylesheets;
    private final Consumer<Scene> onSceneApplier;
    private final Consumer<Parent> onParentApplier;

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
    public Theme(ThemeIdentifier identifier, List<String> stylesheets,
                 Consumer<Scene> onSceneApplier, Consumer<Parent> onParentApplier) {
        this.identifier = Objects.requireNonNull(identifier, "The identifier mustn't be null");
        this.stylesheets = Objects.requireNonNull(stylesheets, "The list of stylesheets mustn't be null");
        this.onSceneApplier = onSceneApplier;
        this.onParentApplier = onParentApplier;
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

    public void apply(Parent parent) {
        Objects.requireNonNull(parent, "The parent mustn't be null");

        parent.getStylesheets().clear();
        if (nonNull(this.onParentApplier)) this.onParentApplier.accept(parent);

        parent.getStylesheets().addAll(stylesheets);
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

    public static void applyDefault(Parent parent) {
        Theme.getDefault().apply(parent);
    }

    /**
     * A ThemeIdentifier used for identify a {@link Theme}.
     *
     * Contains a specific id and a bundleID <b>-></b>
     * that defines the theme name's internationalized key
     */
    public static final class ThemeIdentifier {
        private final String id;
        private final String bundleID;

        public ThemeIdentifier(String id, String bundleID) {
            this.id = Objects.requireNonNull(id, "The theme-id mustn't be null!");
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
