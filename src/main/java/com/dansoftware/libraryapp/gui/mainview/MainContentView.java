package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.mainview.module.googlebooks.GoogleBooksImportModule;
import com.dlsc.workbenchfx.Workbench;
import org.jetbrains.annotations.NotNull;

public class MainContentView extends Workbench implements ContextTransformable {

    private final Context asContext;
    private final Database database;

    MainContentView(@NotNull Database database) {
        this.asContext = Context.from(this);
        this.database = database;
        initModules();
    }

    private void initModules() {
        getModules().add(new GoogleBooksImportModule(asContext, database));
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
