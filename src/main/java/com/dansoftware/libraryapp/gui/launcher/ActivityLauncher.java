package com.dansoftware.libraryapp.gui.launcher;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.login.data.LoginData;
import com.dansoftware.libraryapp.gui.entry.mainview.MainActivity;
import com.dansoftware.libraryapp.main.ArgumentTransformer;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ActivityLauncher implements Runnable {

    private final LauncherMode mode;
    private final DatabaseMeta argument;

    public ActivityLauncher() {
        this(LauncherMode.INIT);
    }

    public ActivityLauncher(@NotNull LauncherMode mode) {
        this(mode, Collections.emptyList());
    }

    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable List<String> params) {
        this(mode, ArgumentTransformer.transform(params));
    }

    public ActivityLauncher(@NotNull LauncherMode mode, @Nullable DatabaseMeta databaseMeta) {
        this.mode = Objects.requireNonNull(mode, "The LauncherMode shouldn't be null");
        this.argument = databaseMeta;
    }

    public void launch() {

        if (mode == LauncherMode.INIT) {
            //if we are in INIT mode

            if (argument == null) {
                //if there was no application-argument

                if (getLoginData().autoLoginTurnedOn()) {
                    //if auto login is turned on

                    Database database = LoginProcessor.of(NitriteDatabase.factory())
                            .onFailed((title, message, t) -> {
                                Platform.runLater(() -> {
                                    var appEntry = new AppEntry(getLoginData());
                                    appEntry.show();
                                    appEntry.showErrorDialog(title, message, (Exception) t);
                                    onActivityLaunched(appEntry);
                                });
                            }).process(getLoginData().getAutoLoginDatabase(), getLoginData().getAutoLoginCredentials());

                    //the login-process was successful
                    if (database != null) {
                        Platform.runLater(() -> {
                            MainActivity mainActivity = new MainActivity(database);
                            mainActivity.show();
                            onActivityLaunched(mainActivity);
                        });
                    }

                } else {
                    //if auto login is turned off
                    Platform.runLater(() -> {
                        AppEntry appEntry = new AppEntry(getLoginData());
                        appEntry.show();
                        onActivityLaunched(appEntry);
                    });
                }

            } else {
                //if there was application-argument
                handleArgument();
            }

        } else if (mode == LauncherMode.ALREADY_RUNNING) {

            if (argument == null) {
                //no argument
                //just focusing on a random window
                AppEntry.getShowingEntries()
                        .stream()
                        .limit(1)
                        .findAny()
                        .ifPresent(AppEntry::requestFocus);
            } else {
                //there is argument

                //if there is an Activity opened with the database we focus on that,
                // otherwise we open a new activity for it
                MainActivity.getByDatabase(argument)
                        .ifPresentOrElse(MainActivity::requestFocus, this::handleArgument);
            }
        }
    }

    private void handleArgument() {
        //we add the launched database to the last databases
        LoginData loginData = getLoginData();
        if (!loginData.getLastDatabases().contains(argument)) {
            loginData.getLastDatabases().add(argument);
            saveLoginData(loginData);
        }

        Database database = LoginProcessor.of(NitriteDatabase.factory())
                .onFailed((title, message, t) -> {
                    Platform.runLater(() -> {
                        LoginData temp = getLoginData();
                        //we select it, but we don't save it to the configurations
                        temp.setSelectedDatabase(argument);

                        var appEntry = new AppEntry(temp);
                        appEntry.show();
                        appEntry.showErrorDialog(title, message, (Exception) t);
                        onActivityLaunched(appEntry);
                    });
                }).process(argument);

        //the login-process was successful
        if (database != null) {
            Platform.runLater(() -> {
                MainActivity mainActivity = new MainActivity(database);
                mainActivity.show();
                onActivityLaunched(mainActivity);
            });
        }

    }


    @Override
    public void run() {
        launch();
    }

    /**
     * Defines how to get the {@link LoginData} for the base {@link ActivityLauncher} object.
     *
     * @return the {@link LoginData} object.
     */
    protected abstract LoginData getLoginData();

    /**
     * Defines how to save the {@link LoginData} for the base {@link ActivityLauncher} object.
     */
    protected abstract void saveLoginData(LoginData loginData);

    /**
     * Called on the UI-thread, when an "Activity" is launched
     *
     * @param context the 'activity' through the {@link Context} interface
     */
    protected abstract void onActivityLaunched(Context context);

}
