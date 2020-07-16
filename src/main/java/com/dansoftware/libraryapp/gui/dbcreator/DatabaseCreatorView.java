package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.theme.ThemeApplier;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.Optional;

/**
 * A DatabaseCreatorView is a gui object that let's the user to
 * create new database files. It should be displayed inside
 * a {@link DatabaseCreatorWindow}.
 */
public class DatabaseCreatorView extends SimpleHeaderView<DatabaseCreatorForm> implements Themeable {

    private final DatabaseCreatorForm form;

    public DatabaseCreatorView() {
        super(I18N.getGeneralWord("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS));
        super.setContent(this.form = new DatabaseCreatorForm(this));
    }

    public Optional<DatabaseMeta> getCreatedAccount() {
        return this.form.getCreatedDb();
    }

    @Override
    public void handleThemeApply(ThemeApplier globalApplier, ThemeApplier customApplier) {
        customApplier.apply(this);
        globalApplier.apply(this);
    }
}
