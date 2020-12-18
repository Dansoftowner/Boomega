package com.dansoftware.libraryapp.launcher;

/**
 * Defines the "modes" of an {@link ActivityLauncher}.
 *
 * @author Daniel Gyorffy
 */
public enum LauncherMode {

    /**
     * A {@link LauncherMode} that should be used with an {@link ActivityLauncher} when the application runs first.
     */
    INIT,

    /**
     * A {@link LauncherMode} that should be used with an {@link ActivityLauncher} when the application is already running.
     */
    ALREADY_RUNNING,

    /**
     * A {@link LauncherMode} that should be used with an  {@link ActivityLauncher} when the application is already running,
     * and the message is not from another process
     */
    INTERNAL
}
