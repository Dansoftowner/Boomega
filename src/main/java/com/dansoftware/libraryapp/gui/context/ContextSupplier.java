package com.dansoftware.libraryapp.gui.context;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContextSupplier} can provide a {@link Context} object.
 *
 * @author Daniel Gyorffy
 */
public interface ContextSupplier {
    @NotNull Context getContext();
}
