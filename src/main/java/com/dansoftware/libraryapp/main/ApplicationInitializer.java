package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfigurationKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * This class is used to initialize some important thing
 * when the application starts.
 *
 * @author Daniel Gyorffy
 * @see Main#init()
 */
public final class ApplicationInitializer {

    private static boolean alreadyInstantiated;
    private static Logger logger = Logger.getLogger(ApplicationInitializer.class.getName());

    public ApplicationInitializer() {
        if (alreadyInstantiated)
            throw new UnsupportedOperationException("Application Initializer cannot be called more than once!");

        alreadyInstantiated = true;
    }

    @Step
    @SuppressWarnings("all")
    private void readConfigurations() {
        ConfigurationHandler.getInstance();
    }

    @Step
    private void setDefaultLocale() {
        Locale.setDefault(
                new Locale(
                        ConfigurationHandler
                                .getInstance()
                                .getConfiguration(PredefinedConfigurationKey.DEFAULT_LOCALE)
                )
        );
    }

    public void init() {
        readConfigurations();
    }


    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    private @interface Step {
    }
}
