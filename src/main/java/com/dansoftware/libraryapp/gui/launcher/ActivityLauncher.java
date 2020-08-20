package com.dansoftware.libraryapp.gui.launcher;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.main.ArgumentTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityLauncher {

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

        //if there was no argument
        if (launched == null) {

            if (mode == LauncherMode.INIT) {

            } else if (mode == LauncherMode.ALREADY_RUNNING) {

            }
        } else {
            //there was an argument

        }
    }
}
