package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBooksImportModule;
import com.dansoftware.libraryapp.gui.rcadd.RecordAddModule;
import com.dlsc.workbenchfx.Workbench;
import org.jetbrains.annotations.NotNull;

public class MainContentView extends Workbench implements ContextTransformable {

    private final Context asContext;
    private final Preferences preferences;
    private final Database database;

    MainContentView(@NotNull Preferences preferences, @NotNull Database database) {
        this.asContext = Context.from(this);
        this.preferences = preferences;
        this.database = database;
        initModules();
    }

    private void initModules() {
        getModules().add(new GoogleBooksImportModule(asContext, preferences, database));
        getModules().add(new RecordAddModule(asContext));
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
