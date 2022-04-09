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

package com.dansoftware.boomega.main;

import com.dansoftware.boomega.instance.ApplicationInstanceService;
import com.restart4j.RestartException;
import javafx.application.Platform;

/**
 * A wrapper for the <b>Restart4j</b> {@link com.restart4j.ApplicationRestart}
 * for Boomega specific reasons.
 *
 * @author Daniel Gyorffy
 */
public class ApplicationRestart {

    @Deprecated
    public ApplicationRestart() {
    }

    /**
     * Restarts the application.
     *
     * @throws RestartException if the restart process doesn't proceeds for some reason
     * @deprecated use the static method {@link #restart()} instead
     */
    @Deprecated
    public void restartApp() throws RestartException {
        restart();
    }

    /**
     * Restarts the application.
     *
     * @throws RestartException if the restart process doesn't proceeds for some reason
     */
    public static void restart() throws RestartException {
        com.restart4j.ApplicationRestart.builder()
                .beforeNewProcessCreated(ApplicationInstanceService::release)
                .terminationPolicy(Platform::exit)
                .build()
                .restartApp();
    }

}
