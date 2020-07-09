package com.dansoftware.libraryapp.gui.entry.login.dbcreator;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.Optional;

public class DatabaseCreatorView extends SimpleHeaderView {

    private final DatabaseCreatorForm form;

    public DatabaseCreatorView() {
        super(I18N.getGeneralWord("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS));
        super.setContent(this.form = new DatabaseCreatorForm(this));
    }

    public Optional<Account> getCreatedAccount() {
        return this.form.getCreatedAccount();
    }
}
