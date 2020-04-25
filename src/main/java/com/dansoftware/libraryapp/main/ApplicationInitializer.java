package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfiguration;
import com.dansoftware.libraryapp.update.UpdateSearcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Locale;

/**
 * This class is used to initialize some important thing
 * when the application starts.
 *
 * @author Daniel Gyorffy
 * @see Main#init()
 */
public final class ApplicationInitializer {

    /**
     * This boolean-field contains that this class already instantiated or not
     */
    private static boolean alreadyInstantiated;

    /**
     * A basic constructor for the object.
     * Cannot be called more than once - otherwise throws
     * an {@link UnsupportedOperationException}
     */
    public ApplicationInitializer() {
        if (alreadyInstantiated)
            throw new UnsupportedOperationException("Application Initializer cannot be called more than once!");

        alreadyInstantiated = Boolean.TRUE;
    }

    @Step
    private void readConfigurations() {
        ConfigurationHandler.getInstance();
    }

    @Step
    private void checkAppRunsFirst() {
        new ApplicationRunsFirstAnalyzer().analyze();
    }

    @Step
    private void setDefaultLocale() {
        Locale.setDefault(
                new Locale(ConfigurationHandler.getInstance().getConfiguration(PredefinedConfiguration.DEFAULT_LOCALE.getKey()))
        );
    }

    @Step
    private void checkUpdates() {
        new UpdateSearcher().search();
    }

    /**
     * This method executes all the tasks that are must be
     * executed before the whole application starts.
     */
    public void init() {
        readConfigurations();
        checkAppRunsFirst();
        setDefaultLocale();
        checkUpdates();
    }

    /**
     * The {@link ApplicationInitializer} uses this annotation
     * on methods that are responsible for executing a sub task in
     * the initialization process
     */
    @Target(ElementType.METHOD)
    private @interface Step {
    }
}
