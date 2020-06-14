package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.main.init.step.StepLink;
import com.dansoftware.libraryapp.main.init.step.impl.ConfigurationReader;
import com.dansoftware.libraryapp.main.init.step.impl.UpdateCheck;

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
        StepLink stepLink = StepLink.builder()
                .step(new ConfigurationReader())
                .step(new UpdateCheck())
                .build();

        stepLink.execute();
    }
}
