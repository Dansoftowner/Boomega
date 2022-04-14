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

package com.dansoftware.boomega.gui.app;

import com.restart4j.RestartException;

import javax.inject.Inject;
import javax.inject.Named;

import static com.dansoftware.boomega.di.DIService.get;

/**
 * Used for restarting the whole application-process.
 * <p>
 *
 * It's designed to work with dependency injection for configuring
 * the sub-tasks it has to perform.
 */
public class ApplicationRestart {

    private final Runnable preProcessCreation;
    private final Runnable terminationPolicy;

    /**
     * Constructs the {@link ApplicationRestart} object with the given policies.
     *
     * @param preProcessCreation the task should be executed before the new Boomega process is created; might be null
     * @param terminationPolicy defines a custom way to terminate the app; might be null
     */
    @Inject
    public ApplicationRestart(
            @Named("preProcessCreation") Runnable preProcessCreation,
            @Named("terminationPolicy") Runnable terminationPolicy
    ) {
        this.preProcessCreation = preProcessCreation;
        this.terminationPolicy = terminationPolicy;
    }

    /**
     * Restarts the application.
     *
     * @throws RestartException if the restart process doesn't proceeds for some reason
     */
    public void restartApp() throws RestartException {
        com.restart4j.ApplicationRestart.builder()
                .beforeNewProcessCreated(preProcessCreation)
                .terminationPolicy(terminationPolicy)
                .build()
                .restartApp();
    }

    /**
     * Constructs an {@link ApplicationRestart} object through the di-service, and
     * uses it for achieving the task.
     *
     * @see #restartApp()
     */
    public static void restart() throws RestartException {
        get(ApplicationRestart.class).restartApp();
    }

}
