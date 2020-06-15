package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.main.init.step.Step;
import com.dansoftware.libraryapp.main.init.step.impl.ConfigurationReadingStep;
import com.dansoftware.libraryapp.main.init.step.impl.UpdateCheckingStep;

/**
 * This class is used to initialize some important thing
 * before the application-gui actually starts.
 *
 * <b>Should be instantiated and used only ONCE</b>
 *
 * @see Main#init()
 */
public final class ApplicationInitializer {

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void initializeApplication() {
        Step configurationReading = new ConfigurationReadingStep();
        configurationReading.execute();

        Step updateChecking = new UpdateCheckingStep();
        updateChecking.execute();
    }
}
