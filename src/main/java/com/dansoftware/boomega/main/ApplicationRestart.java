package com.dansoftware.boomega.main;

import com.dansoftware.boomega.instance.ApplicationInstanceService;
import com.restart4j.RestartException;
import javafx.application.Platform;

/**
 * A wrapper for the <b>Restart4j</b> {@link com.restart4j.ApplicationRestart}
 * for LibraryApp specific reasons.
 *
 * @author Daniel Gyorffy
 */
public class ApplicationRestart {

    /**
     * Restarts the application.
     *
     * @throws RestartException if the restart process doesn't proceeds for some reason
     */
    public void restartApp() throws RestartException {
        com.restart4j.ApplicationRestart.builder()
                .beforeNewProcessCreated(ApplicationInstanceService::release)
                .terminationPolicy(Platform::exit)
                .build()
                .restartApp();
    }

}
