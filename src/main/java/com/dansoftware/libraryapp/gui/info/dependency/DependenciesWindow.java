package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DependenciesWindow extends LibraryAppStage {

    public DependenciesWindow(@NotNull DependencyTable dependencyTable, @Nullable Window owner) {
        super("window.dependencies.title", dependencyTable);
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
    }
}
