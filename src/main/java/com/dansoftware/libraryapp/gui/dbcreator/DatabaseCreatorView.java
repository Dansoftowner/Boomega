package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextSupplier;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
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
public class DatabaseCreatorView extends SimpleHeaderView<DatabaseCreatorForm> implements Themeable, ContextSupplier {

    private final DatabaseCreatorForm form;
    private final Context asContext;

    public DatabaseCreatorView(@NotNull DatabaseTracker databaseTracker) {
        super(I18N.getGeneralWord("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS));
        this.asContext = Context.from(this);
        super.setContent(this.form = new DatabaseCreatorForm(asContext, databaseTracker));
        Theme.registerThemeable(this);
    }

    public DatabaseMeta getCreatedDatabase() {
        return this.form.createdDatabaseProperty().get();
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this);
        newTheme.apply(this);
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
