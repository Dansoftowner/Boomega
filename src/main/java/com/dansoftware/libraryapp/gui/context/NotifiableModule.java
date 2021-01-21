package com.dansoftware.libraryapp.gui.context;

/**
 * A {@link NotifiableModule} represents a GUI module part of a {@link Context}.
 * It's main feature that it can receive an {@link Object} message. That what should be the
 * type of the particular object depends on the particular module.
 *
 * @author Daniel Gyorffy
 */
public interface NotifiableModule {
    void commitData(Object data);
}
