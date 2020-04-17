package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfiguration;
import com.dansoftware.libraryapp.update.UpdateSearcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

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

    @Step(step = 0)
    private void readConfigurations() {
        ConfigurationHandler.getInstance();
    }

    @Step(step = 1)
    private void checkAppRunsFirst() {
        new ApplicationRunsFirstAnalyzer().analyze();
    }

    @Step(step = 2)
    private void setDefaultLocale() {
        Locale.setDefault(
                new Locale(ConfigurationHandler.getInstance().getConfiguration(PredefinedConfiguration.DEFAULT_LOCALE.getKey()))
        );
    }

    @Step(step = 3)
    private void checkUpdates() {
        new UpdateSearcher().search();
    }

    public void init() {
        Stream.of(this.getClass().getDeclaredMethods())
                .filter(method -> Objects.nonNull(method.getAnnotation(Step.class)))
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(Step.class).step()))
                .forEach(method -> {
                    method.setAccessible(true);

                    try {
                        method.invoke(this);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface Step {
        int step();
    }
}
