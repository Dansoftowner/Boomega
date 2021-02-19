package com.dansoftware.boomega.gui.context;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContextTransformable} can provide a {@link Context} object.
 *
 * @author Daniel Gyorffy
 */
public interface ContextTransformable {
    @NotNull Context getContext();
}