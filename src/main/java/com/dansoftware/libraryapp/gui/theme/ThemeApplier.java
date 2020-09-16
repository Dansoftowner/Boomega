package com.dansoftware.libraryapp.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

/**
 * A ThemeApplier can apply a particular design on a GUI ({@link Scene} or {@link Parent})
 * element; it's used by {@link Theme} objects.
 *
 * <p>
 * One ThemeApplier may not set the full design; {@link Theme}s usually use
 * two separate {@link ThemeApplier} objects to get the job done.
 */
public interface ThemeApplier {

    /**
     * Applies the design on a scene.
     *
     * @param scene the Scene object; shouldn't be null
     */
    void apply(@NotNull Scene scene);

    /**
     * Applies the design on a {@link Parent} object.
     *
     * @param parent the gui-object; shouldn't be null
     */
    void apply(@NotNull Parent parent);

    /**
     * Removes the particular design from the scene.
     *
     * @param scene the Scene object; shouldn't be null
     */
    void applyBack(@NotNull Scene scene);

    /**
     * Removes the particular design from the {@link Parent} object.
     *
     * @param parent the gui-object; shouldn't be null
     */
    void applyBack(@NotNull Parent parent);

    /**
     * Creates an empty {@link ThemeApplier} that doesn't do anything.
     *
     * @return the {@link ThemeApplier} object.
     */
    static ThemeApplier empty() {
        return new ThemeApplier() {
            @Override
            public void apply(@NotNull Scene scene) {
            }

            @Override
            public void apply(@NotNull Parent parent) {
            }

            @Override
            public void applyBack(@NotNull Scene scene) {
            }

            @Override
            public void applyBack(@NotNull Parent parent) {
            }
        };
    }
}
