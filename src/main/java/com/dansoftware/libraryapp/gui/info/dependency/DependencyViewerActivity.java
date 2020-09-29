package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.entry.Context;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link DependencyViewerActivity} used for launching a {@link DependenciesWindow} with
 * a {@link DependencyTable} easily.
 *
 * @author Daniel Gyorffy
 */
public class DependencyViewerActivity {

    private Context context;

    public DependencyViewerActivity(@NotNull Context context) {
        this.context = context;
    }

    public void show() {
        var table = new DependencyTable(DependencyLister.listDependencies());
        var window = new DependenciesWindow(table, context.getContextWindow());
        window.show();
    }
}
