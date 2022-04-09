/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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
     * Should be used only once during the program execution when the application starts.
     */
    INITIAL,

    /**
     * Should be used when the application is already running, for internal operations
     */
    INTERNAL,

    /**
     * Should be used when the application is already running and the request is from an external process.
     */
    EXTERNAL

}
