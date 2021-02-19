package com.dansoftware.boomega.main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * An abstract {@link Application} implementation.
 *
 * <p>
 * The {@link Application#init()} method must be overridden but the {@link Application#start(Stage)}
 * method doesn't.
 *
 * <p>
 * A {@link BaseApplication} should be launched through the {@link #launchApp(Class, String...)} method.
 *
 * @author Daniel Gyorffy
 */
public abstract class BaseApplication extends Application {

    @Override
    public abstract void init() throws Exception;

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    protected void notifyPreloader(String i18n) {
        notifyPreloader(new Preloader.MessageNotification(i18n));
    }

    protected void hidePreloader() {
        notifyPreloader(new Preloader.HideNotification());
    }

    protected void showPreloader() {
        notifyPreloader(new Preloader.ShowNotification());
    }

    /**
     * Returns the application-arguments <i>(command-line params)</i> in a {@link List}.
     *
     * <p>
     * It's a basic form of {@code getParameters().getRaw()}.
     *
     * @return the list of arguments
     * @see Parameters#getRaw()
     */
    public List<String> getApplicationArgs() {
        return getParameters().getRaw();
    }

    protected boolean hasParameters() {
        return !getApplicationArgs().isEmpty();
    }

    protected <T> Optional<T> getFormattedArgument(@NotNull Function<List<String>, T> argumentsConverter) {
        if (!hasParameters())
            return Optional.empty();
        return Optional.ofNullable(argumentsConverter.apply(getApplicationArgs()));
    }

    /**
     * Launches the base-application with a {@link Preloader}.
     *
     * @param appClass the class-reference to the {@link BaseApplication} implementation
     * @param args     the application-arguments
     */
    public static void launchApp(Class<? extends BaseApplication> appClass, String... args) {
        LauncherImpl.launchApplication(appClass, Preloader.class, args);
    }
}
