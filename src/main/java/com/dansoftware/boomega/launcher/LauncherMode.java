/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.launcher;

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
