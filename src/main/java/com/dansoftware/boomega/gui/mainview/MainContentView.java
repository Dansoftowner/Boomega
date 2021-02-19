package com.dansoftware.boomega.gui.mainview;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.googlebooks.GoogleBooksImportModule;
import com.dansoftware.boomega.gui.record.add.RecordAddModule;
import com.dansoftware.boomega.gui.record.show.RecordsViewModule;
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
        getModules().add(new GoogleBooksImportModule(asContext, preferences));
        getModules().add(new RecordAddModule(asContext, database));
        getModules().add(new RecordsViewModule(asContext, preferences, database));
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
