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

package com.dansoftware.boomega.process

/**
 * A [SingletonProcessService] is responsible for preventing multiple application processes.
 */
interface SingletonProcessService {

    /**
     * Opens the singleton-process service. Normally, it consists of these steps:
     * - Deciding the application is already running or not
     * - If yes, somehow sending the arguments to the already running process and terminating the current process
     *
     * > Should be called when the application starts running, as early as possible.
     *
     * @param args the application arguments received
     */
    fun open(args: Array<String>)

    /**
     * Closes the singleton-process service.
     * It releases possible resources/background working threads.
     *
     * > Should be called before the application terminates.
     */
    fun release()
}