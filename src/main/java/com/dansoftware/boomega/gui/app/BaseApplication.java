/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.gui.preloader.Preloader;
import com.dansoftware.boomega.main.ArgumentParser;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nls;

import java.util.List;
import java.util.Optional;

import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

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

    protected void notifyPreloader(@Nls String i18n) {
        notifyPreloader(new Preloader.MessageNotification(i18n(i18n)));
    }

    protected void progress(double value) {
        notifyPreloader(new Preloader.ProgressNotification(value));
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

    protected Optional<DatabaseMeta> getParsedArgument() {
        if (!hasParameters())
            return Optional.empty();
        return Optional.ofNullable(ArgumentParser.parse(getApplicationArgs()));
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
