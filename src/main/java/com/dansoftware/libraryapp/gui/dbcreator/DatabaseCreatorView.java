package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.i18n.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import org.jetbrains.annotations.NotNull;

/**
 * A DatabaseCreatorView is a gui object that let's the user to
 * create new database files. It should be displayed inside
 * a {@link DatabaseCreatorWindow}.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseCreatorView extends SimpleHeaderView<DatabaseCreatorForm> implements ContextTransformable {

    private final DatabaseCreatorForm form;
    private final Context asContext;

    public DatabaseCreatorView(@NotNull DatabaseTracker databaseTracker) {
        super(I18N.getValue("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS));
        this.asContext = Context.from(this);
        super.setContent(this.form = new DatabaseCreatorForm(asContext, databaseTracker));
    }

    public DatabaseMeta getCreatedDatabase() {
        return this.form.createdDatabaseProperty().get();
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
