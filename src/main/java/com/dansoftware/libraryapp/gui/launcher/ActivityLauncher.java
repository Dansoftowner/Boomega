package com.dansoftware.libraryapp.gui.launcher;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.db.NitriteDatabase;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
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
    private DatabaseMeta launched;

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
        this.launched = databaseMeta;
    }

    public void launch() {

        if (mode == LauncherMode.INIT) {
            //if we are in INIT mode

            if (launched == null) {
                //if there was no application-argument

                if (getLoginData().autoLoginTurnedOn()) {
                    //if auto login is turned on

                    Database database = new LoginProcessor(NitriteDatabase.factory())
                            .onFailed((title, message, t) -> {
                                Platform.runLater(() -> {
                                    var appEntry = new AppEntry(getLoginData());
                                    appEntry.show();
                                    //and showing the error message!!!!
                                });
                            }).process(getLoginData().getAutoLoginDatabase(), getLoginData().getAutoLoginCredentials());

                    //the login-process was successful
                    if (database != null) {
                        Platform.runLater(() -> {
                            new MainActivity(database).show();
                        });
                    }

                } else {
                    //if auto login is turned off
                    Platform.runLater(() -> {
                        new AppEntry(getLoginData()).show();
                    });
                }

            } else {
                //if there was application-argument

                //we add the launched database to the last databases
                argumentAvailable();
            }

        } else if (mode == LauncherMode.ALREADY_RUNNING) {

            if (launched == null) {
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
                MainActivity.getByDatabase(launched)
                        .ifPresentOrElse(MainActivity::requestFocus, this::argumentAvailable);
            }
        }
    }

    private void argumentAvailable() {
        //we add the launched database to the last databases
        if (!getLoginData().getLastDatabases().contains(launched)) {
            getLoginData().getLastDatabases().add(launched);
            saveLoginData();
        }

        Database database = new LoginProcessor(NitriteDatabase.factory())
                .onFailed((title, message, t) -> {
                    Platform.runLater(() -> {
                        LoginData temp = getLoginData();
                        //we select it, but we don't save it to the configurations
                        temp.setSelectedDatabase(launched);

                        var appEntry = new AppEntry(temp);
                        appEntry.show();
                        //with error-messages!!!!!
                    });
                }).process();

        //the login-process was successful
        if (database != null) {
            Platform.runLater(() -> {
                new MainActivity(database).show();
            });
        }

    }


    @Override
    public void run() {
        launch();
    }

    protected abstract LoginData getLoginData();

    protected abstract void saveLoginData();


}
